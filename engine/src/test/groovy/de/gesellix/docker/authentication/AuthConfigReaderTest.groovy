package de.gesellix.docker.authentication

import de.gesellix.docker.engine.DockerEnv
import de.gesellix.testutil.ResourceReader
import spock.lang.Requires
import spock.lang.Specification

import static de.gesellix.docker.authentication.AuthConfig.EMPTY_AUTH_CONFIG

class AuthConfigReaderTest extends Specification {

  DockerEnv env
  AuthConfigReader authConfigReader

  def setup() {
    env = Mock(DockerEnv)
    authConfigReader = Spy(AuthConfigReader, constructorArgs: [env])
  }

  def "read authConfig (new format)"() {
    given:
    String oldDockerConfig = System.clearProperty("docker.config")
    File expectedConfigFile = new ResourceReader().getClasspathResourceAsFile('/auth/config.json', AuthConfigReader)
    env.indexUrl_v1 >> 'https://index.docker.io/v1/'

    when:
    def result = authConfigReader.readAuthConfig(null, expectedConfigFile)

    then:
    result == new AuthConfig(username: "gesellix",
                             password: "-yet-another-password-",
                             email: "tobias@gesellix.de",
                             serveraddress: "https://index.docker.io/v1/")

    cleanup:
    if (oldDockerConfig) {
      System.setProperty("docker.config", oldDockerConfig)
    }
  }

  def "read authConfig (legacy format)"() {
    given:
    String oldDockerConfig = System.clearProperty("docker.config")
    File expectedConfigFile = new ResourceReader().getClasspathResourceAsFile('/auth/dockercfg', AuthConfigReader)
    env.indexUrl_v1 >> 'https://index.docker.io/v1/'

    when:
    def result = authConfigReader.readAuthConfig(null, expectedConfigFile)

    then:
    result == new AuthConfig(username: "gesellix",
                             password: "-yet-another-password-",
                             email: "tobias@gesellix.de",
                             serveraddress: "https://index.docker.io/v1/")

    cleanup:
    if (oldDockerConfig) {
      System.setProperty("docker.config", oldDockerConfig)
    }
  }

  def "read auth config for official Docker index"() {
    given:
    env.indexUrl_v1 >> 'https://index.docker.io/v1/'
    File dockerCfg = new ResourceReader().getClasspathResourceAsFile('/auth/config.json', AuthConfigReader)

    when:
    def authDetails = authConfigReader.readAuthConfig(null, dockerCfg)

    then:
    authDetails.username == "gesellix"
    and:
    authDetails.password == "-yet-another-password-"
    and:
    authDetails.email == "tobias@gesellix.de"
    and:
    authDetails.serveraddress == "https://index.docker.io/v1/"
  }

  def "read auth config for quay.io"() {
    given:
    File dockerCfg = new ResourceReader().getClasspathResourceAsFile('/auth/config.json', AuthConfigReader)

    when:
    def authDetails = authConfigReader.readAuthConfig("quay.io", dockerCfg)

    then:
    authDetails.username == "gesellix"
    and:
    authDetails.password == "-a-password-for-quay-"
    and:
    authDetails.email == "tobias@gesellix.de"
    and:
    authDetails.serveraddress == "quay.io"
  }

  def "read auth config for missing config file"() {
    given:
    File nonExistingFile = new File('./I should not exist')
    assert !nonExistingFile.exists()

    when:
    def authDetails = authConfigReader.readAuthConfig(null, nonExistingFile)

    then:
    authDetails == new AuthConfig()
  }

  def "read auth config for unknown registry hostname"() {
    given:
    File dockerCfg = new ResourceReader().getClasspathResourceAsFile('/auth/config.json', AuthConfigReader)

    when:
    def authDetails = authConfigReader.readAuthConfig("unknown.example.com", dockerCfg)

    then:
    authDetails == EMPTY_AUTH_CONFIG
  }

  @Requires({ System.properties['user.name'] == 'gesellix' })
  def "read default docker config file using credsStore"() {
    given:
    String oldDockerConfig = System.clearProperty("docker.config")
    String configFile = "/auth/dockercfg-with-credsStore-${System.properties['os.name'].toString().toLowerCase().capitalize().replaceAll("\\s", "_")}"
    File expectedConfigFile = new ResourceReader().getClasspathResourceAsFile(configFile, AuthConfigReader)
    env.indexUrl_v1 >> 'https://index.docker.io/v1/'
    env.getDockerConfigFile() >> expectedConfigFile

    when:
    def authConfig = authConfigReader.readDefaultAuthConfig()

    then:
    1 * authConfigReader.readAuthConfig(null, expectedConfigFile)
    authConfig.serveraddress == "https://index.docker.io/v1/"
    authConfig.username == "gesellix"
    authConfig.password =~ ".+"

    cleanup:
    if (oldDockerConfig) {
      System.setProperty("docker.config", oldDockerConfig)
    }
  }

  def "read default authConfig"() {
    given:
    String oldDockerConfig = System.clearProperty("docker.config")
    File expectedConfigFile = new ResourceReader().getClasspathResourceAsFile('/auth/config.json', AuthConfigReader)
    env.indexUrl_v1 >> 'https://index.docker.io/v1/'
    env.getDockerConfigFile() >> expectedConfigFile

    when:
    def result = authConfigReader.readDefaultAuthConfig()

    then:
    1 * authConfigReader.readAuthConfig(null, expectedConfigFile)
    result == new AuthConfig(username: "gesellix",
                             password: "-yet-another-password-",
                             email: "tobias@gesellix.de",
                             serveraddress: "https://index.docker.io/v1/")

    cleanup:
    if (oldDockerConfig) {
      System.setProperty("docker.config", oldDockerConfig)
    }
  }
}
