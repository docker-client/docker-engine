package de.gesellix.docker.engine;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

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

  @Override
  public void onResponse(@NotNull final Call call, @NotNull final Response response) throws IOException {
    TcpUpgradeVerificator.ensureTcpUpgrade(response);

    if (attachConfig.getStreams().getStdin() != null) {
      // pass input from the client via stdin and pass it to the output stream
      // running it in an own thread allows the client to gain back control
      final Source stdinSource = Okio.source(attachConfig.getStreams().getStdin());
      Thread writer = new Thread(() -> {
        try {
          final BufferedSink bufferedSink = Okio.buffer(getConnectionProvider().getSink());
          long written = bufferedSink.writeAll(stdinSource);
          log.warn("xxxxx - writer - written " + written);
          bufferedSink.flush();
          log.warn("xxxxx - writer - flushed");
          attachConfig.onSinkWritten(response);
          log.warn("xxxxx - writer - onSinkWritten");
          CountDownLatch done = new CountDownLatch(1);
          delayed(100, "writer", () -> {
            log.warn("xxxxx - writer - delayed");
            try {
              bufferedSink.close();
              log.warn("xxxxx - writer - delayed closed");
              attachConfig.onSinkClosed(response);
              log.warn("xxxxx - writer - delayed onSinkClosed");
            }
            catch (Exception e) {
              log.warn("error", e);
            }
            log.warn("xxxxx - writer - delayed return");
            return null;
          }, done);
          boolean inTime = done.await(5, TimeUnit.SECONDS);
          if (!inTime) {
            log.warn("xxxxx - writer - done timeout");
          }
        }
        catch (InterruptedException e) {
          log.debug("stdin->sink interrupted", e);
          Thread.currentThread().interrupt();
        }
        catch (Exception e) {
          onFailure(e);
        }
        finally {
          log.trace("writer finished");
        }
      });
      writer.setName("stdin-writer " + call.request().url().encodedPath());
      writer.start();
    }
    else {
      log.debug("no stdin.");
    }

    if (attachConfig.getStreams().getStdout() != null) {
      final BufferedSink bufferedStdout = Okio.buffer(Okio.sink(attachConfig.getStreams().getStdout()));
      Thread reader = new Thread(() -> {
        try {
          log.warn("xxxxx - reader - writeAll -> " + getConnectionProvider().getSource());
          bufferedStdout.writeAll(getConnectionProvider().getSource());
          log.warn("xxxxx - reader - flush");
          bufferedStdout.flush();
          log.warn("xxxxx - reader - flushed");
          CountDownLatch done = new CountDownLatch(1);
          delayed(100, "reader", () -> {
            log.warn("xxxxx - reader - delay ...");
            attachConfig.onSourceConsumed();
            log.warn("xxxxx - reader - delay onSourceConsumed");
            return null;
          }, done);
          boolean inTime = done.await(5, TimeUnit.SECONDS);
          if (!inTime) {
            log.warn("xxxxx - reader - done timeout");
          }
        }
        catch (InterruptedException e) {
          log.debug("source->stdout interrupted", e);
          Thread.currentThread().interrupt();
        }
        catch (Exception e) {
          onFailure(e);
        }
        finally {
          log.trace("reader finished");
        }
      });
      reader.setName("stdout-reader " + call.request().url().encodedPath());
      reader.start();
    }
    else {
      log.debug("no stdout.");
    }

    attachConfig.onResponse(response);
  }

  public static void delayed(long delay, String name, final Supplier<?> action, final CountDownLatch done) {
    new Timer(true).schedule(new TimerTask() {
      @Override
      public void run() {
        Thread.currentThread().setName("Delayed " + name + " action (" + Thread.currentThread().getName() + ")");
        try {
          action.get();
        }
        catch (Exception e) {
          log.warn("xxxxx - delayed - error", e);
          throw e;
        }
        finally {
          log.warn("xxxxx - delayed - done");
          done.countDown();
          log.warn("xxxxx - delayed - cancel");
          cancel();
        }
      }
    }, delay);
  }

  public ConnectionProvider getConnectionProvider() {
    return connectionProvider;
  }
}
