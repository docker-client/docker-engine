package de.gesellix.docker.engine;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okio.Buffer;
import okio.Okio;
import okio.Pipe;
import okio.Sink;
import okio.Source;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.BiConsumer;

public class OkResponseCallback implements Callback {

  private static final Logger log = LoggerFactory.getLogger(OkResponseCallback.class);

  private final ConnectionProvider connectionProvider;
  private final AttachConfig attachConfig;

  public OkResponseCallback(ConnectionProvider connectionProvider, AttachConfig attachConfig) {
    this.connectionProvider = connectionProvider;
    this.attachConfig = attachConfig;
  }

  @Override
  public void onFailure(@NotNull Call call, @NotNull final IOException e) {
    log.error("connection failed: " + e.getMessage(), e);
    attachConfig.onFailure(e);
  }

  public void onFailure(Exception e) {
    log.error("error", e);
    attachConfig.onFailure(e);
  }

  /** Reads all bytes from {@code source} and writes them to {@code sink}. */
  private Long readAll(Source source, Sink sink) throws IOException {
    long result = 0L;
//    Okio.buffer(sink).writeAll(source);
    Buffer buffer = new Buffer();
    for (long count; (count = source.read(buffer, 1024)) != -1L; result += count) {
      sink.write(buffer, count);
    }
    return result;
  }

  /** Calls {@link #readAll} on a background thread. */
  private Future<Long> readAllAsync(final Source source, final Sink sink) {
    ExecutorService executor = Executors.newSingleThreadExecutor();
    try {
      return executor.submit(() -> readAll(source, sink));
    }
    finally {
      executor.shutdown();
    }
  }

  private Thread transfer(Source source, Sink sink, BiConsumer<Long, Long> onFinish) {
    return new Thread(() -> {
      Pipe p = new Pipe(1024);
      try {
        Future<Long> futureSink = readAllAsync(p.source(), sink);
        Future<Long> futureSource = readAllAsync(source, p.sink());
        Long read = futureSource.get();
        p.sink().flush();
        p.sink().close();
        Long written = futureSink.get();
        onFinish.accept(read, written);
      }
      catch (Exception e) {
        log.warn("error", e);
        onFailure(e);
      }
    });
  }

  @Override
  public void onResponse(@NotNull final Call call, @NotNull final Response response) throws IOException {
    TcpUpgradeVerificator.ensureTcpUpgrade(response);

    if (attachConfig.getStreams().getStdin() != null) {
      // client's stdin -> socket
      Thread writer = transfer(
          Okio.source(attachConfig.getStreams().getStdin()),
          connectionProvider.getSink(),
          (read, written) -> {
            log.warn("read {}, written {}", read, written);
            attachConfig.onStdInConsumed(response);
            attachConfig.onSinkClosed(response);
          });
      writer.setName("stdin-writer " + call.request().url().encodedPath());
      writer.start();
    }
    else {
      log.debug("no stdin.");
    }

    if (attachConfig.getStreams().getStdout() != null) {
      // client's stdout <- socket
      Thread reader = transfer(
          connectionProvider.getSource(),
          Okio.sink(attachConfig.getStreams().getStdout()),
          (read, written) -> {
            log.warn("read {}, written {}", read, written);
            attachConfig.onStdOutConsumed();
          });
      reader.setName("stdout-reader " + call.request().url().encodedPath());
      reader.start();
    }
    else {
      log.debug("no stdout.");
    }

    attachConfig.onResponse(response);
  }
}
