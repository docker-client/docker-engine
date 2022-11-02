package de.gesellix.docker.engine

import de.gesellix.testutil.ResourceReader
import spock.lang.Specification

class DockerEnvTest extends Specification {

  def "read configured docker config.json"() {
    given:
    def expectedConfigDir = new File('.').absoluteFile
    def oldDockerConfigDir = System.setProperty("docker.config", expectedConfigDir.absolutePath)
    DockerEnv env = new DockerEnv()

    when:
    def dockerConfigFile = env.getDockerConfigFile()

    then:
    dockerConfigFile.absolutePath == new File(expectedConfigDir, 'config.json').absolutePath

    cleanup:
    if (oldDockerConfigDir) {
      System.setProperty("docker.config", oldDockerConfigDir)
    } else {
      System.clearProperty("docker.config")
    }
  }

  def "read default docker config file"() {
    given:
    def expectedConfigFile = new ResourceReader().getClasspathResourceAsFile('/auth/config.json', getClass())
    def oldDockerConfigDir = System.setProperty("docker.config", expectedConfigFile.parent)
    DockerEnv env = new DockerEnv()

    when:
    File actualConfigFile = env.getDockerConfigFile()

    then:
    actualConfigFile == expectedConfigFile

    cleanup:
    if (oldDockerConfigDir) {
      System.setProperty("docker.config", oldDockerConfigDir)
    }
  }

  def "read legacy docker config file"() {
    given:
    DockerEnv env = new DockerEnv()
    def oldDockerConfig = System.clearProperty("docker.config")

    def nonExistingFile = new File('./I should not exist')
    assert !nonExistingFile.exists()
    env.dockerConfigReader.configFile = nonExistingFile

    def expectedConfigFile = new ResourceReader().getClasspathResourceAsFile('/auth/dockercfg', getClass())
    env.dockerConfigReader.legacyConfigFile = expectedConfigFile

    env.resetDockerHostFromCurrentConfig()

    when:
    def actualConfigFile = env.getDockerConfigFile()

    then:
    actualConfigFile == expectedConfigFile

    cleanup:
    if (oldDockerConfig) {
      System.setProperty("docker.config", oldDockerConfig)
    }
  }
}
