package de.gesellix.docker.engine

import de.gesellix.docker.context.DockerContextResolver
import de.gesellix.testutil.ResourceReader
import spock.lang.Specification

class DockerConfigReaderTest extends Specification {

  private DockerConfigReader reader

  def setup() {
    reader = new DockerConfigReader()
  }

  def "reads the Docker config file"() {
    given:
    File configFile = new ResourceReader().getClasspathResourceAsFile('/context/config.json', DockerContextResolver)

    when:
    Map configFileContent = reader.readDockerConfigFile(configFile)

    then:
    configFileContent.get("credsStore") == "desktop"
    configFileContent.get("currentContext") == "for-test"
  }
}
