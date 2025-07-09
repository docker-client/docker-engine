package de.gesellix.docker.hijack;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.gesellix.docker.engine.AttachConfig;
import de.gesellix.docker.rawstream.Frame;
import de.gesellix.docker.rawstream.FrameReader;
import okhttp3.Connection;
import okhttp3.Interceptor;
import okhttp3.Response;
import okio.Buffer;
import okio.Sink;
import okio.Socket;
import okio.Source;

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

    // Only proceed with hijacking if the response indicates an upgrade (101) or is otherwise successful.
    // The stdin == null check should not prevent consumption of the server's output.
    if (!(response.code() == 101 || response.isSuccessful())) {
      return response;
    }
//    TcpUpgradeVerificator.ensureTcpUpgrade(response);

    Socket socket = response.socket();
    if (socket == null) {
      // If it was a 101 but no socket, something is wrong.
      return response;
    }

    Source source = socket.getSource();

    // Always start reader if the socket is established (code 101 implies it)
    Thread reader = new Thread(() -> {
      log.debug("reader thread started for source={}, stdout={}", source, stdout);
      Buffer buffer = new Buffer();
      long totalBytesRead = 0;
      try {
        log.debug("expect multiplexed response (no tty): {}", attachConfig.isExpectMultiplexedResponse());

        FrameReader frameReader = new FrameReader(source, attachConfig.isExpectMultiplexedResponse());
        Frame frame;
        while ((frame = frameReader.readNext(Frame.class)) != null) {
          if (frame.getPayload() != null) {
            int payloadLength = frame.getPayload().length;
            log.debug("reader: read frame with payload length {}", payloadLength);
            buffer.write(frame.getPayload());
            // Log the first few bytes of the data
//            if (payloadLength > 0) {
//              byte[] data = buffer.peek().readByteArray(Math.min(payloadLength, 16));
//              log.debug("reader: First 16 bytes of data: {}", bytesToHex(data));
//            }
            stdout.write(buffer, payloadLength);
            stdout.flush();
            log.debug("reader: wrote {} bytes to stdout.", payloadLength);
            totalBytesRead += payloadLength;
          } else {
            log.debug("reader: read frame with null payload.");
          }
        }
        log.debug("reader: finished reading from source. Total bytes read: {}. Invoking onSourceConsumed().", totalBytesRead);
        attachConfig.onSourceConsumed();
      } catch (Exception e) {
        log.error("Error during source to stdout transfer", e);
        attachConfig.onFailure(e);
        // Do not rethrow the exception here as it would terminate the thread, and the finally block
        // would not be guaranteed to run if the exception occurs early in the thread's lifecycle.
        // The onFailure callback already signals the failure.
        //         throw new RuntimeException(e); // Rethrow to mark thread as failed
      } finally {
        log.debug("reader finally block entered.");
        // IMPORTANT: Ensure stdout and the socket\'s source are closed
        try {
          if (stdout != null) {
            stdout.close();
            log.debug("stdout closed.");
          }
        } catch (IOException e) {
          log.error("Failed to close stdout", e);
        }
        try {
          if (source != null) {
            source.close(); // Close the socket's input stream
            log.debug("source stream closed.");
          }
        } catch (IOException e) {
          log.error("Failed to close source stream", e);
        }
      }
    });
    reader.setName("reader-" + System.identityHashCode(chain.request()));
    reader.setUncaughtExceptionHandler((thread, exception) -> log.error("Uncaught exception in reader", exception));
    reader.start();

    Sink sink = socket.getSink();
    // Start writer thread only if stdin is provided
    if (stdin != null) {
      Thread writer = new Thread(() -> {
        log.info("writer thread started for stdin={}, sink={}", stdin, sink);
        Buffer buffer = new Buffer();
        try {
          for (long byteCount; (byteCount = stdin.read(buffer, 8192L)) != -1; ) {
            sink.write(buffer, byteCount);
            sink.flush();
          }
          attachConfig.onSinkWritten(response);
        } catch (Exception e) {
          log.error("Error during stdin to sink transfer", e);
          attachConfig.onFailure(e);
          throw new RuntimeException(e); // Rethrow to mark thread as failed
        } finally {
          // IMPORTANT: Ensure the sink is closed to signal EOF to the server
          try {
            if (sink != null) { // Defensive check
              try {
                // Don't sink.close() too quickly,
                // let the reader have enough time to read all bytes
                Thread.sleep(500);
              } catch (InterruptedException ignored) {
              }
              sink.close();
            }
          } catch (IOException e) {
            log.error("Failed to close sink", e);
          }
          attachConfig.onSinkClosed(response); // Callback after sink is closed
        }
      });
      writer.setName("writer-" + System.identityHashCode(chain.request()));
      writer.setUncaughtExceptionHandler((thread, exception) -> log.error("Uncaught exception in writer", exception));
      writer.start();
    } else {
      // If stdin is null, immediately close the sink to signal EOF to the server's input.
      // This is crucial for processes that expect an EOF on stdin to terminate.
      try {
        sink.close();
        attachConfig.onSinkClosed(response); // Callback
      } catch (IOException e) {
        log.error("Failed to close sink when stdin is null", e);
        attachConfig.onFailure(e);
      }
    }

    attachConfig.onResponse(response);
    return response;
  }

  private static String bytesToHex(byte[] bytes) {
    StringBuilder sb = new StringBuilder();
    for (byte b : bytes) {
      sb.append(String.format("%02x", b));
    }
    return sb.toString();
  }
}
