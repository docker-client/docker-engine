package de.gesellix.docker.engine;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;
import okio.Source;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

public class StreamingRequestBody extends RequestBody {

  private final MediaType contentType;
  private final Source body;

  public StreamingRequestBody(MediaType contentType, Source body) {
    this.contentType = contentType;
    this.body = body;
  }

  @Nullable
  @Override
  public MediaType contentType() {
    return contentType;
  }

  @Override
  public long contentLength() {
    return -1;
  }

  @Override
  public boolean isOneShot() {
    return true;
  }

  @Override
  public void writeTo(@NotNull BufferedSink bufferedSink) throws IOException {
    Throwable exception = null;
    try {
      bufferedSink.writeAll(body);
    }
    catch (Exception e) {
      exception = e;
      throw e;
    }
    finally {
      if (exception == null) {
        body.close();
      }
      else {
        try {
          body.close();
        }
        catch (Throwable closeException) {
          // ignored, previous exception takes precendence
        }
      }
    }
  }
}
