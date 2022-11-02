package de.gesellix.docker.context;

import java.util.Map;

public class DockerContext {
  String description;

  // e.g. `"StackOrchestrator": "swarm"`
  Map<String, Object> additionalFields;

  public DockerContext(String description) {
    this.description = description;
  }
}
