package de.gesellix.docker.engine;

import okhttp3.Connection;
import okhttp3.Interceptor;
import okhttp3.Response;
import okio.Okio;
import okio.Sink;
import okio.Source;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class ConnectionProvider implements Interceptor {

  private static final Logger log = LoggerFactory.getLogger(ConnectionProvider.class);
  private Sink sink = null;
  private Source source = null;

  @NotNull
  @Override
  public Response intercept(Chain chain) throws IOException {
    // attention: this connection is *per request*, so sink and source might be overwritten
    Connection connection = chain.connection();
    if (connection == null) {
      throw new IllegalStateException("Connection is null. This one should only be used in a network interceptor, not in an application interceptor.");
    }

    if (source != null) {
      log.warn("overwriting source");
    }
    source = Okio.source(connection.socket());

    if (sink != null) {
      log.warn("overwriting sink");
    }
    sink = Okio.sink(connection.socket());

    return chain.proceed(chain.request());
  }

  public Sink getSink() {
    return sink;
  }

  public Source getSource() {
    return source;
  }
}
