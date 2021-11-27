package de.gesellix.docker.hijack;

import de.gesellix.docker.engine.AttachConfig;
import de.gesellix.docker.rawstream.Frame;
import de.gesellix.docker.rawstream.FrameReader;
import okhttp3.Connection;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okio.Buffer;
import okio.BufferedSink;
import okio.Okio;
import okio.Sink;
import okio.Source;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class HijackingInterceptor implements Interceptor {

  private static final Logger log = LoggerFactory.getLogger(HijackingInterceptor.class);

  private final AttachConfig attachConfig;
  private final Source stdin;
  private final Sink stdout;

  public HijackingInterceptor(AttachConfig attachConfig, Source stdin, Sink stdout) {
    this.attachConfig = attachConfig;
    this.stdin = stdin;
    this.stdout = stdout;
  }

  @Override
  public Response intercept(Interceptor.Chain chain) throws IOException {
    Connection connection = chain.connection();
    if (connection == null) {
      throw new IllegalStateException("Connection is null. This one should only be used as a network interceptor, not as application interceptor.");
    }

    Sink sink = Okio.sink(connection.socket());
    Source source = Okio.source(connection.socket());

    Request originalRequest = chain.request();
    Request modifiedRequest = originalRequest;
    if (stdin != null) {
      modifiedRequest = originalRequest.newBuilder()
          .method(originalRequest.method(), originalRequest.body())
          .header("transfer-encoding", "chunked")
//        .tag(new HijackedSink(sink))
          .build();
    }

//    chain.connection().socket().setSoTimeout(0);
    Response response = chain.proceed(modifiedRequest);

    if (!(response.code() == 101 || response.isSuccessful()) || stdin == null) {
      return response;
    }
//    TcpUpgradeVerificator.ensureTcpUpgrade(response);

    chain.call().timeout().clearTimeout().clearDeadline();

    // stdin -> sink
    Thread stdin2sink = new Thread(() -> {
      Buffer tmpBuffer = new Buffer();
      try (BufferedSink bufferedSink = Okio.buffer(sink)) {
        long count = 0;
        while (bufferedSink.isOpen()) {
          long n = stdin.read(tmpBuffer, 1024);
          if (n < 0) {
            log.warn("finished after " + count + " bytes");
            attachConfig.onSinkWritten(response);
            break;
          }
          count += n;
          bufferedSink.write(tmpBuffer, n);
          bufferedSink.flush();
//          attachConfig.onBytesWrittenToSink(n, count);
        }
      }
      catch (Exception e) {
        log.error("error", e);
        attachConfig.onFailure(e);
        throw new RuntimeException(e);
      }
      attachConfig.onSinkClosed(response);
    });
    stdin2sink.setName("stdin2sink-" + System.identityHashCode(originalRequest));
    stdin2sink.setUncaughtExceptionHandler((thread, exception) -> log.error("", exception));
    stdin2sink.setDaemon(true);
    stdin2sink.start();

    // source -> stdout
    Thread source2stdout = new Thread(() -> {
      Buffer tmpBuffer = new Buffer();
      try (BufferedSink bufferedSink = Okio.buffer(stdout)) {
        long count = 0;

        if (true || attachConfig.isExpectMultiplexedResponse()) {
          FrameReader frameReader = new FrameReader(source, attachConfig.isExpectMultiplexedResponse());
          Frame frame;
          while ((frame = frameReader.readNext(Frame.class)) != null) {
//          while (bufferedSink.isOpen()) {
//            frame = frameReader.readNext(Frame.class);
            if (frame != null && frame.getPayload() != null) {
              count += frame.getPayload().length;
//            tmpBuffer.write(frame.getPayload());
              bufferedSink.write(frame.getPayload());
              bufferedSink.flush();
            }
          }
        }
        else {
          while (bufferedSink.isOpen()) {
            long n = source.read(tmpBuffer, 1024);
            if (n < 0) {
              break;
            }
            count += n;
            bufferedSink.write(tmpBuffer, n);
            bufferedSink.flush();
          }
        }
      }
      catch (Exception e) {
        log.error("error", e);
        attachConfig.onFailure(e);
        throw new RuntimeException(e);
      }
      attachConfig.onSourceConsumed();
    });
    source2stdout.setName("source2stdout-" + System.identityHashCode(originalRequest));
    source2stdout.setUncaughtExceptionHandler((thread, exception) -> log.error("", exception));
    source2stdout.setDaemon(true);
    source2stdout.start();

    attachConfig.onResponse(response);
    return response;
  }
}
