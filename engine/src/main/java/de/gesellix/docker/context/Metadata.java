package de.gesellix.docker.context;

import java.util.HashMap;
import java.util.Map;

public class Metadata {
  private String name;

  private Object metadata;

  private Map<String, Object> endpoints = new HashMap<>();

  public Metadata(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public Object getMetadata() {
    return metadata;
  }

  public void setMetadata(Object metadata) {
    this.metadata = metadata;
  }

  public Map<String, Object> getEndpoints() {
    return endpoints;
  }
}
