package de.gesellix.util;

import okio.BufferedSink;
import okio.Okio;
import okio.Sink;
import okio.Source;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class IOUtils {

  public static long consumeToDevNull(InputStream source) throws IOException {
    return copy(Okio.source(source), Okio.blackhole());
  }

  public static long copy(InputStream source, OutputStream sink) throws IOException {
    return copy(Okio.source(source), Okio.sink(sink));
  }

  public static long copy(Source source, Sink sink) throws IOException {
    BufferedSink buffer = Okio.buffer(sink);
    long count = buffer.writeAll(source);
    buffer.flush();
    return count;
  }

  public static String toString(InputStream source) throws IOException {
    return Okio.buffer(Okio.source(source)).readUtf8();
  }

  public static void closeQuietly(InputStream stream) {
    try {
      if (stream != null) {
        stream.close();
      }
    }
    catch (Exception ignored) {
    }
  }

  public static void closeQuietly(Source source) {
    try {
      if (source != null) {
        source.close();
      }
    }
    catch (Exception ignored) {
    }
  }
}
