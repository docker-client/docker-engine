package de.gesellix.docker.context

import de.gesellix.testutil.ResourceReader
import spock.lang.Specification
import spock.lang.Unroll

class MetadataStoreTest extends Specification {
  private MetadataStore store

  def setup() {
    File configFile = new ResourceReader().getClasspathResourceAsFile('/context/config.json', MetadataStore)
    File dockerContextStoreDir = new File(configFile.getParentFile(), "contexts")
    store = new MetadataStore(new File(dockerContextStoreDir, MetadataStore.metadataDir))
  }

  @Unroll
  def "should hex-encode the SHA-256 digest of '#contextName' to '#contextDir'"() {
    when:
    String directoryName = store.getContextDir(contextName)

    then:
    directoryName == contextDir

    where:
    contextName     | contextDir
    "for-test"      | "297dc204469307b573ca1e71dead5336f61c3aa222bf3a507cd59bf0c07a43b8"
    "desktop-linux" | "fe9c6bd7a66301f49ca9b6a70b217107cd1284598bfc254700c989b916da791e"
  }

  def "should read metadata"() {
    when:
    Metadata metadata = store.getMetadata("for-test")

    then:
    metadata.name == "for-test"
    metadata.metadata == [:]
    metadata.endpoints == [
        docker: new EndpointMetaBase("unix:///var/run/docker.sock", false)
    ]
  }
}
