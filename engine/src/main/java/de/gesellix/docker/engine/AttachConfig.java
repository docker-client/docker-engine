package de.gesellix.docker.engine;

import okhttp3.Response;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.function.Function;
import java.util.function.Supplier;

public class AttachConfig {

  private final boolean expectMultiplexedResponse;
  private final Streams streams;
  private final Callbacks callbacks;

  public AttachConfig() {
    this(false);
  }

  public AttachConfig(boolean expectMultiplexedResponse) {
    this.expectMultiplexedResponse = expectMultiplexedResponse;
    this.streams = new Streams();
    this.callbacks = new Callbacks();
  }

  public Streams getStreams() {
    return streams;
  }

  public Object onFailure(Exception e) {
    return callbacks.onFailure.apply(e);
  }

  public void setOnFailure(Function<Exception, ?> onFailure) {
    callbacks.onFailure = onFailure;
  }

  public Object onResponse(Response r) {
    return callbacks.onResponse.apply(r);
  }

  public void setOnResponse(Function<Response, ?> onResponse) {
    callbacks.onResponse = onResponse;
  }

  public Object onSinkClosed(Response r) {
    return callbacks.onSinkClosed.apply(r);
  }

  public void setOnSinkClosed(Function<Response, ?> onSinkClosed) {
    callbacks.onSinkClosed = onSinkClosed;
  }

  public Object onSinkWritten(Response r) {
    return callbacks.onSinkWritten.apply(r);
  }

  public void setOnSinkWritten(Function<Response, ?> onSinkWritten) {
    callbacks.onSinkWritten = onSinkWritten;
  }

  public Object onSourceConsumed() {
    return callbacks.onSourceConsumed.get();
  }

  public void setOnSourceConsumed(Supplier<?> onSourceConsumed) {
    callbacks.onSourceConsumed = onSourceConsumed;
  }

  public boolean isExpectMultiplexedResponse() {
    return expectMultiplexedResponse;
  }

  public static class Streams {

    private InputStream stdin;
    private OutputStream stdout;
    private OutputStream stderr;

    public Streams() {
      this.stdin = null;
      this.stdout = System.out;
      this.stderr = System.err;
    }

    public InputStream getStdin() {
      return stdin;
    }

    public void setStdin(InputStream stdin) {
      this.stdin = stdin;
    }

    public OutputStream getStdout() {
      return stdout;
    }

    public void setStdout(OutputStream stdout) {
      this.stdout = stdout;
    }

    public OutputStream getStderr() {
      return stderr;
    }

    public void setStderr(OutputStream stderr) {
      this.stderr = stderr;
    }
  }

  public static class Callbacks {

    private Function<Exception, ?> onFailure = (Exception e) -> null;
    private Function<Response, ?> onResponse = (Response r) -> null;
    private Function<Response, ?> onSinkClosed = (Response r) -> null;
    private Function<Response, ?> onSinkWritten = (Response r) -> null;
    private Supplier<?> onSourceConsumed = () -> null;
  }
}
