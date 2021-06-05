package de.gesellix.docker.engine;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EngineRequest {

  RequestMethod method;
  String path;
  Map<String, String> headers = new HashMap<>();
  Map<String, List<String>> query = new HashMap<>();

  String requestContentType = null;
  Object body = null;

  int timeout = 0;

  boolean async = false;
  AttachConfig attach = null;
  OutputStream stdout;

  String apiVersion = null;

  public EngineRequest(RequestMethod method, String path) {
    this.method = method;
    this.path = path;
  }
}
