package de.gesellix.docker.engine

import groovy.util.logging.Slf4j
import okhttp3.Connection
import okhttp3.Interceptor
import okhttp3.Response
import okio.Okio
import okio.Sink
import okio.Source

@Slf4j
class ConnectionProvider implements Interceptor {

  Sink sink = null
  Source source = null

  @Override
  Response intercept(Interceptor.Chain chain) throws IOException {
    // attention: this connection is *per request*, so sink and source might be overwritten
    Connection connection = chain.connection()
    if (source != null) {
      log.warn("overwriting source")
    }
    source = Okio.source(connection.socket())
    if (sink != null) {
      log.warn("overwriting sink")
    }
    sink = Okio.sink(connection.socket())

    Response response = chain.proceed(chain.request())
    return response
  }
}
