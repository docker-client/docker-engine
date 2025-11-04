package de.gesellix.docker.engine

import groovy.util.logging.Slf4j
import okio.Okio
import spock.lang.IgnoreIf
import spock.lang.Requires
import spock.lang.Specification

import static de.gesellix.docker.engine.RequestMethod.GET
import static de.gesellix.docker.engine.TestConstants.CONSTANTS

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
    def client = new OkDockerClient()
    when:
    def response = client.request(new EngineRequest(GET, "/info"))
    then:
    def dockerHost = client.getDockerClientConfig().getEnv().getDockerHost()
    dockerHost.startsWith("unix://")
    response.status.code == 200
  }

  @Requires({ LocalDocker.isNamedPipe() })
  def "should support named pipe socket connections (Docker for Windows)"() {
    def client = new OkDockerClient()
    when:
    def response = client.request(new EngineRequest(GET, "/info"))
    then:
    def dockerHost = client.getDockerClientConfig().getEnv().getDockerHost()
    dockerHost.startsWith("npipe://")
    response.status.code == 200
  }
}
