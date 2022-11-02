package de.gesellix.docker.context

import de.gesellix.docker.engine.DockerConfigReader
import de.gesellix.testutil.ResourceReader
import spock.lang.Specification

class DockerContextResolverTest extends Specification {

  private DockerContextResolver dockerContextResolver

  def setup() {
    dockerContextResolver = new DockerContextResolver()
  }

  def "resolve context"() {
    given:
    File configFile = new ResourceReader().getClasspathResourceAsFile('/context/config.json', DockerContextResolver)
    DockerConfigReader reader = new DockerConfigReader()
    reader.dockerConfigFile = configFile

    when:
    String contextName = dockerContextResolver.resolveDockerContextName(reader)

    then:
    contextName == "for-test"
  }

  def "resolve endpoint"() {
    given:
    File configFile = new ResourceReader().getClasspathResourceAsFile('/context/config.json', DockerContextResolver)
    File dockerContextStoreDir = new File(configFile.getParentFile(), "contexts");
    ContextStore contextStore = new ContextStore(dockerContextStoreDir)

    when:
    EndpointMetaBase endpoint = dockerContextResolver.resolveDockerEndpoint(contextStore, "for-test")

    then:
    endpoint.host == "unix:///var/run/docker.sock"
  }
}
