package de.gesellix.docker.response;

import okio.BufferedSource;
import okio.Okio;
import okio.Source;

import java.io.IOException;

public class LineReader implements Reader {

  private final BufferedSource buffer;

  public LineReader(Source source) {
    this.buffer = Okio.buffer(source);
  }

  @Override
  public Object readNext() throws IOException {
    return buffer.readUtf8Line();
  }

  @Override
  public boolean hasNext() throws IOException {
    return !Thread.currentThread().isInterrupted() && !buffer.exhausted();
  }
}
