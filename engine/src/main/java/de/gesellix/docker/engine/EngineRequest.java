package de.gesellix.docker.engine;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class EngineRequest {

  private RequestMethod method;
  private String path;
  private Map<String, String> headers = new HashMap<>();
  private Map<String, List<String>> query = new HashMap<>();

  private String contentType = null;
  private Object body = null;

  private int timeout = 0;

  private boolean async = false;
  private OutputStream stdout;

  private String apiVersion = null;

  public EngineRequest(RequestMethod method, String path) {
    this.method = method;
    this.path = path;
  }

  public RequestMethod getMethod() {
    return method;
  }

  public void setMethod(RequestMethod method) {
    this.method = method;
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public Map<String, String> getHeaders() {
    return headers;
  }

  public void setHeaders(Map<String, String> headers) {
    this.headers = headers;
  }

  public Map<String, List<String>> getQuery() {
    return query;
  }

  public void setQuery(Map<String, List<String>> query) {
    this.query = query;
  }

  public String getContentType() {
    return contentType;
  }

  public void setContentType(String contentType) {
    this.contentType = contentType;
  }

  public Object getBody() {
    return body;
  }

  public void setBody(Object body) {
    this.body = body;
  }

  public int getTimeout() {
    return timeout;
  }

  public void setTimeout(int timeout) {
    this.timeout = timeout;
  }

  public boolean isAsync() {
    return async;
  }

  public void setAsync(boolean async) {
    this.async = async;
  }

  public OutputStream getStdout() {
    return stdout;
  }

  public void setStdout(OutputStream stdout) {
    this.stdout = stdout;
  }

  public String getApiVersion() {
    return apiVersion;
  }

  public void setApiVersion(String apiVersion) {
    this.apiVersion = apiVersion;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) { return true; }
    if (o == null || getClass() != o.getClass()) { return false; }
    EngineRequest that = (EngineRequest) o;
    return timeout == that.timeout && async == that.async && method == that.method && Objects.equals(path, that.path) && Objects.equals(headers, that.headers) &&
           Objects.equals(query, that.query) && Objects.equals(contentType, that.contentType) && Objects.equals(body, that.body) &&
           Objects.equals(stdout, that.stdout) && Objects.equals(apiVersion, that.apiVersion);
  }

  @Override
  public int hashCode() {
    return Objects.hash(method, path, headers, query, contentType, body, timeout, async, stdout, apiVersion);
  }
}
