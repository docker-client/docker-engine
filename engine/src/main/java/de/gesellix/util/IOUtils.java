package de.gesellix.util;

import okio.BufferedSink;
import okio.Okio;
import okio.Sink;
import okio.Source;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class IOUtils {

  public static long copy(InputStream source, OutputStream sink) throws IOException {
    return copy(Okio.source(source), Okio.sink(sink));
  }

  public static long copy(Source source, Sink sink) throws IOException {
    long count = 0;
    BufferedSink buffer = Okio.buffer(sink);
    while (buffer.isOpen()) {
      long n = source.read(buffer.getBuffer(), 8192L);
      if (n < 1) {
        break;
      }
      count += n;
    }
    buffer.flush();
    return count;
  }

  public static String toString(InputStream source) throws IOException {
    return Okio.buffer(Okio.source(source)).readUtf8();
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
