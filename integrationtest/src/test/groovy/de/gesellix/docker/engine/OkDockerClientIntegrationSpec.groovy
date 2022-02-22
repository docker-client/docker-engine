package de.gesellix.docker.engine

import groovy.util.logging.Slf4j
import okhttp3.Response
import okio.Okio
import spock.lang.Ignore
import spock.lang.IgnoreIf
import spock.lang.Requires
import spock.lang.Specification
import spock.lang.Unroll

import java.util.concurrent.CountDownLatch

import static de.gesellix.docker.engine.RequestMethod.GET
import static de.gesellix.docker.engine.TestConstants.CONSTANTS
import static java.util.concurrent.TimeUnit.SECONDS

@Slf4j
@Requires({ LocalDocker.available() })
class OkDockerClientIntegrationSpec extends Specification {

  final static dockerHubUsername = "gesellix"
  final static dockerHubPassword = "-yet-another-password-"
  final static dockerHubEmail = "tobias@gesellix.de"

  def "should allow GET requests"() {
    given:
    def client = new OkDockerClient()

    when:
    def ping = client.get([path: "/_ping"])
    def content = ping.content ?: Okio.buffer(Okio.source(client.get([path: "/_ping"]).stream)).readUtf8()

    then:
    content == "OK"
  }

  def "should allow POST requests"() {
    given:
    def client = new OkDockerClient()
    def request = [path : "/images/create",
                   query: [fromImage: CONSTANTS.imageRepo,
                           tag      : CONSTANTS.imageTag,
                           registry : ""]]

    when:
    def response = client.post(request)
    then:
    response.content.last() in [
        [status: "Status: Image is up to date for ${CONSTANTS.imageName}".toString()],
        [status: "Status: Downloaded newer image for ${CONSTANTS.imageName}".toString()]
    ]
  }

  @IgnoreIf({ dockerHubPassword == "-yet-another-password-" })
  def "should allow POST requests with body"() {
    given:
    def client = new OkDockerClient()
    def authDetails = ["username"     : dockerHubUsername,
                       "password"     : dockerHubPassword,
                       "email"        : dockerHubEmail,
                       "serveraddress": "https://index.docker.io/v1/"]
    def request = [path              : "/auth",
                   body              : authDetails,
                   requestContentType: "application/json"]
    when:
    def response = client.post(request)
    then:
    response.content == [IdentityToken: "",
                         Status       : "Login Succeeded"]
  }

  def "should optionally stream a response"() {
    def client = new OkDockerClient()
    def outputStream = new ByteArrayOutputStream()
    when:
    client.get([path  : "/_ping",
                stdout: outputStream])
    then:
    outputStream.toString() == "OK"
  }

  def "should parse application/json"() {
    def client = new OkDockerClient()
    when:
    def response = client.get([path: "/version"])
    then:
    def content = response.content

    def nonMatchingEntries = CONSTANTS.versionDetails.findResults { key, matcher ->
      !matcher(content[key]) ? [(key): content[key]] : null
    }
    nonMatchingEntries.empty
  }

  @Requires({ LocalDocker.isUnixSocket() })
  def "should support unix socket connections (Linux native or Docker for Mac)"() {
    def client = new OkDockerClient("unix:///var/run/docker.sock")
    when:
    def response = client.request(new EngineRequest(GET, "/info"))
    then:
    response.status.code == 200
  }

  @Requires({ LocalDocker.isNamedPipe() })
  def "should support named pipe socket connections (Docker for Windows)"() {
    def client = new OkDockerClient("npipe:////./pipe/docker_engine")
    when:
    def response = client.request(new EngineRequest(GET, "/info"))
    then:
    response.status.code == 200
  }

  @Ignore("Attach is broken")
  @Unroll
  def "attach (openStdin: #openStdin, tty: #tty)"() {
    given:
    def client = new OkDockerClient()

    // pull image (ensure it exists locally)
    client.post([path : "/images/create",
                 query: [fromImage: CONSTANTS.imageName]])
    // create container
    def containerConfig = [
        Tty       : tty,
        OpenStdin : openStdin,
        Image     : CONSTANTS.imageName,
        Entrypoint: ["/cat"]
    ]
    String containerId = client.post([path              : "/containers/create".toString(),
                                      query             : [name: ""],
                                      body              : containerConfig,
                                      requestContentType: "application/json"]).content.Id
    // start container
    client.post([path              : "/containers/${containerId}/start".toString(),
                 requestContentType: "application/json"])
    // resize container TTY
//    client.post([path : "/containers/${containerId}/attach/resize".toString(),
//                 query: [h: 46, w: 158]])
    // inspect container
//    boolean multiplexStreams = !client.get([path: "/containers/${containerId}/json".toString()]).content.Config.Tty

    String content = "attach ${UUID.randomUUID()}"
    String expectedOutput = containerConfig.Tty ? "$content\r\n$content\r\n" : "$content\n"

    def stdout = new ByteArrayOutputStream(expectedOutput.length())
    def stdin = new PipedOutputStream()

    def onSinkClosed = new CountDownLatch(1)
    def onSinkWritten = new CountDownLatch(1)
    def onSourceConsumed = new CountDownLatch(1)

    def attachConfig = new AttachConfig(!containerConfig.Tty)
    attachConfig.streams.stdin = new PipedInputStream(stdin)
//    attachConfig.streams.stdout = stdout
    attachConfig.streams.stdout = new TeeOutputStream(stdout, System.out)
    attachConfig.onResponse = { Response response ->
      log.info("[attach (interactive)] got response")
    }
    attachConfig.onSinkClosed = { Response response ->
      log.info("[attach (interactive)] sink closed (complete: ${stdout.toString() == expectedOutput})\n${stdout.toString()}")
      onSinkClosed.countDown()
    }
    attachConfig.onSinkWritten = { Response response ->
      log.info("[attach (interactive)] sink written (complete: ${stdout.toString() == expectedOutput})\n${stdout.toString()}")
      onSinkWritten.countDown()
    }
    attachConfig.onSourceConsumed = {
      if (stdout.toString() == expectedOutput) {
        log.info("[attach (interactive)] consumed (complete: ${stdout.toString() == expectedOutput})\n${stdout.toString()}")
        onSourceConsumed.countDown()
      }
      else {
        log.info("[attach (interactive)] consumed (complete: ${stdout.toString() == expectedOutput})\n${stdout.toString()}")
      }
    }
//    new OkDockerClient().post([
    client.post([
        path  : "/containers/${containerId}/attach".toString(),
        query : [logs: true, stream: true, stdin: true, stdout: true, stderr: true],
        attach: attachConfig])

    when:
    stdin.write("$content\n".bytes)
    stdin.flush()
    stdin.close()
    boolean sinkWritten = onSinkWritten.await(5, SECONDS)
    boolean sinkClosed = onSinkClosed.await(5, SECONDS)
    boolean sourceConsumed = onSourceConsumed.await(5, SECONDS)

    then:
    sinkClosed
    sinkWritten
    sourceConsumed
    stdout.size() > 0
    stdout.toByteArray() == expectedOutput.bytes

    cleanup:
    client.post([path: "/containers/${containerId}/stop".toString(), query: [t: 10]])
    client.post([path: "/containers/${containerId}/wait".toString()])
    client.delete([path: "/containers/${containerId}".toString(), query: [:]])

    where:
    tty   | openStdin
    false | true
//    true  | true // TODO: fix on Windows
  }
}
