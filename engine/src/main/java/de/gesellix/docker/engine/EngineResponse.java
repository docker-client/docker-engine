package de.gesellix.docker.engine;

import java.io.InputStream;
import java.util.concurrent.Future;

public class EngineResponse<R> {

  private EngineResponseStatus status = new EngineResponseStatus();
  private Object headers;
  private String contentType;
  private String mimeType;
  private String contentLength;
  private InputStream stream;
  private R content;
  private Future<?> taskFuture;
  private OkResponseCallback responseCallback;

  public EngineResponseStatus getStatus() {
    return status;
  }

  public void setStatus(EngineResponseStatus status) {
    this.status = status;
  }

  public Object getHeaders() {
    return headers;
  }

  public void setHeaders(Object headers) {
    this.headers = headers;
  }

  public String getContentType() {
    return contentType;
  }

  public void setContentType(String contentType) {
    this.contentType = contentType;
  }

  public String getMimeType() {
    return mimeType;
  }

  public void setMimeType(String mimeType) {
    this.mimeType = mimeType;
  }

  public String getContentLength() {
    return contentLength;
  }

  public void setContentLength(String contentLength) {
    this.contentLength = contentLength;
  }

  public InputStream getStream() {
    return stream;
  }

  public void setStream(InputStream stream) {
    this.stream = stream;
  }

  public R getContent() {
    return content;
  }

  public void setContent(R content) {
    this.content = content;
  }

  public Future<?> getTaskFuture() {
    return taskFuture;
  }

  public void setTaskFuture(Future<?> taskFuture) {
    this.taskFuture = taskFuture;
  }

  public OkResponseCallback getResponseCallback() {
    return responseCallback;
  }

  public void setResponseCallback(OkResponseCallback responseCallback) {
    this.responseCallback = responseCallback;
  }

  @Override
  public String toString() {
    return "EngineResponse{" +
           "status=" + status +
           ", headers=" + headers +
           ", contentType='" + contentType + '\'' +
           ", mimeType='" + mimeType + '\'' +
           ", contentLength='" + contentLength + '\'' +
           ", stream=" + stream +
           ", content=" + content +
           ", taskFuture=" + taskFuture +
           ", responseCallback=" + responseCallback +
           '}';
  }
}
