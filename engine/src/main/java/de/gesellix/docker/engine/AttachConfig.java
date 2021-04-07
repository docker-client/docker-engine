package de.gesellix.docker.engine;

import groovy.lang.Closure;
import okhttp3.Response;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.function.Function;
import java.util.function.Supplier;

public class AttachConfig {

  private final Streams streams;
  private final Callbacks callbacks;

  public AttachConfig() {
    streams = new Streams();
    callbacks = new Callbacks();
  }

  public Streams getStreams() {
    return streams;
  }

  public Callbacks getCallbacks() {
    return callbacks;
  }

  public Object onFailure(Exception e) {
    return callbacks.onFailure.apply(e);
  }

  public void setOnFailure(Function<Exception, ?> onFailure) {
    callbacks.onFailure = onFailure;
  }

  /**
   * @see #setOnFailure(Function)
   * @deprecated Will be removed after migration from Groovy to plain Java
   */
  @Deprecated
  public void setOnFailure(Closure<?> onFailure) {
    callbacks.onFailure = onFailure::call;
  }

  public Object onResponse(Response r) {
    return callbacks.onResponse.apply(r);
  }

  public void setOnResponse(Function<Response, ?> onResponse) {
    callbacks.onResponse = onResponse;
  }

  /**
   * @see #setOnResponse(Function)
   * @deprecated Will be removed after migration from Groovy to plain Java
   */
  @Deprecated
  public void setOnResponse(Closure<?> onResponse) {
    callbacks.onResponse = onResponse::call;
  }

  public Object onSinkClosed(Response r) {
    return callbacks.onSinkClosed.apply(r);
  }

  public void setOnSinkClosed(Function<Response, ?> onSinkClosed) {
    callbacks.onSinkClosed = onSinkClosed;
  }

  /**
   * @see #setOnSinkClosed(Function)
   * @deprecated Will be removed after migration from Groovy to plain Java
   */
  @Deprecated
  public void setOnSinkClosed(Closure<?> onSinkClosed) {
    callbacks.onSinkClosed = onSinkClosed::call;
  }

  public Object onSinkWritten(Response r) {
    return callbacks.onSinkWritten.apply(r);
  }

  public void setOnSinkWritten(Function<Response, ?> onSinkWritten) {
    callbacks.onSinkWritten = onSinkWritten;
  }

  /**
   * @see #setOnSinkWritten(Function)
   * @deprecated Will be removed after migration from Groovy to plain Java
   */
  @Deprecated
  public void setOnSinkWritten(Closure<?> onSinkWritten) {
    callbacks.onSinkWritten = onSinkWritten::call;
  }

  public Object onSourceConsumed() {
    return callbacks.onSourceConsumed.get();
  }

  public void setOnSourceConsumed(Supplier<?> onSourceConsumed) {
    callbacks.onSourceConsumed = onSourceConsumed;
  }

  /**
   * @see #setOnSourceConsumed(Supplier)
   * @deprecated Will be removed after migration from Groovy to plain Java
   */
  @Deprecated
  public void setOnSourceConsumed(Closure<?> onSourceConsumed) {
    callbacks.onSourceConsumed = () -> {
      onSourceConsumed.call();
      return null;
    };
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

    public Function<Exception, ?> getOnFailure() {
      return onFailure;
    }

    public void setOnFailure(Function<Exception, ?> onFailure) {
      this.onFailure = onFailure;
    }

    public Function<Response, ?> getOnResponse() {
      return onResponse;
    }

    public void setOnResponse(Function<Response, ?> onResponse) {
      this.onResponse = onResponse;
    }

    public Function<Response, ?> getOnSinkClosed() {
      return onSinkClosed;
    }

    public void setOnSinkClosed(Function<Response, ?> onSinkClosed) {
      this.onSinkClosed = onSinkClosed;
    }

    public Function<Response, ?> getOnSinkWritten() {
      return onSinkWritten;
    }

    public void setOnSinkWritten(Function<Response, ?> onSinkWritten) {
      this.onSinkWritten = onSinkWritten;
    }

    public Supplier<?> getOnSourceConsumed() {
      return onSourceConsumed;
    }

    public void setOnSourceConsumed(Supplier<?> onSourceConsumed) {
      this.onSourceConsumed = onSourceConsumed;
    }
  }
}
