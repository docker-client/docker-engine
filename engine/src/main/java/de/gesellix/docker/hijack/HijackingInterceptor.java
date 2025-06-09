package de.gesellix.docker.hijack;

import de.gesellix.docker.engine.AttachConfig;
import de.gesellix.docker.rawstream.Frame;
import de.gesellix.docker.rawstream.FrameReader;
import okhttp3.Connection;
import okhttp3.Interceptor;
import okhttp3.Response;
import okhttp3.internal.http1.Streams;
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

    Response response = chain.proceed(chain.request());

    if (!(response.code() == 101 || response.isSuccessful()) || stdin == null) {
      return response;
    }
//    TcpUpgradeVerificator.ensureTcpUpgrade(response);

    Streams streams = response.streams();
    if (streams == null) {
      throw new IllegalStateException("Streams is null. This one should only be used as a network interceptor, not as application interceptor.");
    }

    // source -> stdout
    Thread source2stdout = new Thread(() -> {
      try (BufferedSink bufferedSink = Okio.buffer(stdout)) {
        if (attachConfig.isExpectMultiplexedResponse()) {
          FrameReader frameReader = new FrameReader(streams.getSource(), attachConfig.isExpectMultiplexedResponse());
          Frame frame;
          while ((frame = frameReader.readNext(Frame.class)) != null) {
            if (frame != null && frame.getPayload() != null) {
              bufferedSink.write(frame.getPayload());
              bufferedSink.flush();
            }
          }
        } else {
          Buffer tmp = new Buffer();
          for (long byteCount; (byteCount = streams.getSource().read(tmp, 8192L)) != -1; ) {
            bufferedSink.write(tmp, byteCount);
            bufferedSink.flush();
          }
        }

        // TODO how to make it work without that timeout?
        Thread.sleep(2000);
      } catch (Exception e) {
        log.error("error", e);
        attachConfig.onFailure(e);
        throw new RuntimeException(e);
      }
      attachConfig.onSourceConsumed();
    });
    source2stdout.setName("source2stdout-" + System.identityHashCode(chain.request()));
    source2stdout.setUncaughtExceptionHandler((thread, exception) -> log.error("", exception));
    source2stdout.setDaemon(true);
    source2stdout.start();

    // stdin -> sink
    Thread stdin2sink = new Thread(() -> {
      try (BufferedSink bufferedSink = Okio.buffer(streams.getSink())) {
        Buffer tmp = new Buffer();
        for (long byteCount; (byteCount = stdin.read(tmp, 8192L)) != -1; ) {
          bufferedSink.write(tmp, byteCount);
          bufferedSink.flush();
        }
        attachConfig.onSinkWritten(response);

        // TODO how to make it work without that timeout?
        Thread.sleep(2000);
      } catch (Exception e) {
        log.error("error", e);
        attachConfig.onFailure(e);
        throw new RuntimeException(e);
      }
      attachConfig.onSinkClosed(response);
    });
    stdin2sink.setName("stdin2sink-" + System.identityHashCode(chain.request()));
    stdin2sink.setUncaughtExceptionHandler((thread, exception) -> log.error("", exception));
    stdin2sink.setDaemon(true);
    stdin2sink.start();

    attachConfig.onResponse(response);
    return response;
  }
}
