package de.gesellix.docker.engine

import groovy.util.logging.Slf4j
import okhttp3.Response
import spock.lang.IgnoreIf
import spock.lang.Requires
import spock.lang.Specification

import java.util.concurrent.CountDownLatch

import static de.gesellix.docker.engine.TestConstants.CONSTANTS
import static java.util.concurrent.TimeUnit.SECONDS

@Slf4j
@Requires({ LocalDocker.available() })
class OkDockerClientIntegrationSpec extends Specification {

    final static dockerHubUsername = "gesellix"
    final static dockerHubPassword = "-yet-another-password-"
    final static dockerHubEmail = "tobias@gesellix.de"

    def "should allow GET requests"() {
        def client = new OkDockerClient()
        expect:
        client.get([path: "/_ping"]).content == "OK"
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
    "should allow POST requests with body"() {
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
    "should support unix socket connections (Linux native or Docker for Mac)"() {
        def client = new OkDockerClient("unix:///var/run/docker.sock")
        when:
        def response = client.request([method: "GET",
                                       path  : "/info"])
        then:
        response.status.code == 200
    }

    @Requires({ LocalDocker.isNamedPipe() })
    "should support named pipe socket connections (Docker for Windows)"() {
        def client = new OkDockerClient("npipe:////./pipe/docker_engine")
        when:
        def response = client.request([method: "GET",
                                       path  : "/info"])
        then:
        response.status.code == 200
    }

    def "attach (interactive)"() {
        given:
        def client = new OkDockerClient()

        // pull image (ensure it exists locally)
        client.post([path   : "/images/create",
                     query  : [fromImage: CONSTANTS.imageRepo,
                               tag      : CONSTANTS.imageTag],
                     headers: [EncodedRegistryAuth: "."]])
        // create container
        def containerConfig = [
                Tty      : true,
                OpenStdin: true,
                Image    : CONSTANTS.imageName,
                Cmd      : ["/bin/sh", "-c", "read line && echo \"->\$line\""]
        ]
        String containerId = client.post([path              : "/containers/create".toString(),
                                          query             : [name: ""],
                                          body              : containerConfig,
                                          requestContentType: "application/json"]).content.Id
        // start container
        client.post([path              : "/containers/${containerId}/start".toString(),
                     requestContentType: "application/json"])
        // inspect container
        def multiplexStreams = !client.get([path: "/containers/${containerId}/json"]).content.Config.Tty

        def content = "attach ${UUID.randomUUID()}"
        def expectedOutput = "$content\r\n->$content\r\n"

        def outputStream = new ByteArrayOutputStream() {

            @Override
            synchronized void write(byte[] b, int off, int len) {
                log.info("write ${off}/${len} to ${b.length} bytes")
                super.write(b, off, len)
            }
        }
        def inputStream = new ByteArrayInputStream("$content\n".bytes) {

            @Override
            synchronized int read(byte[] b, int off, int len) {
                log.info("read ${off}/${len} from ${b.length} bytes")
                return super.read(b, off, len)
            }
        }

        def onSinkClosed = new CountDownLatch(1)
        def onSourceConsumed = new CountDownLatch(1)

        def attachConfig = new AttachConfig()
        attachConfig.streams.stdin = inputStream
        attachConfig.streams.stdout = outputStream
        attachConfig.onSinkClosed = { Response response ->
            log.info("[attach (interactive)] sink closed \n${outputStream.toString()}")
            onSinkClosed.countDown()
        }
        attachConfig.onSourceConsumed = {
            if (outputStream.toByteArray() == expectedOutput.bytes) {
                log.info("[attach (interactive)] fully consumed \n${outputStream.toString()}")
                onSourceConsumed.countDown()
            }
            else {
                log.info("[attach (interactive)] partially consumed \n${outputStream.toString()}")
            }
        }

        when:
//        def response =
        client.post([path            : "/containers/${containerId}/attach".toString(),
                     query           : [stream: 1, stdin: 1, stdout: 1, stderr: 1],
                     attach          : attachConfig,
                     multiplexStreams: multiplexStreams])
        def sinkClosed = onSinkClosed.await(5, SECONDS)
        def sourceConsumed = onSourceConsumed.await(5, SECONDS)

        then:
        sinkClosed
        sourceConsumed
        outputStream.size() > 0
        outputStream.toByteArray() == expectedOutput.bytes

        cleanup:
        client.post([path : "/containers/${containerId}/stop".toString(),
                     query: [t: 10]])
        client.post([path: "/containers/${containerId}/wait".toString()])
        client.delete([path : "/containers/${containerId}".toString(),
                       query: [:]])
    }
}
