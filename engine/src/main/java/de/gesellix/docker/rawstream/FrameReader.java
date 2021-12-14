package de.gesellix.docker.rawstream;

import de.gesellix.docker.response.Reader;
import okio.Buffer;
import okio.BufferedSource;
import okio.Okio;
import okio.Source;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.EOFException;
import java.io.IOException;

public class FrameReader implements Reader<Frame> {

  private final static Logger log = LoggerFactory.getLogger(FrameReader.class);

  private final BufferedSource bufferedSource;
  private boolean expectMultiplexedResponse;

  private final Buffer buffer = new Buffer();

  public FrameReader(Source source) {
    this(source, false);
  }

  public FrameReader(Source source, boolean expectMultiplexedResponse) {
    this.bufferedSource = Okio.buffer(source);
    this.expectMultiplexedResponse = expectMultiplexedResponse;
  }

  @Override
  public Frame readNext(Class<Frame> type) {
    if (expectMultiplexedResponse) {
      // See https://docs.docker.com/engine/api/v1.41/#operation/ContainerAttach for the stream format documentation.
      // header := [8]byte{STREAM_TYPE, 0, 0, 0, SIZE1, SIZE2, SIZE3, SIZE4}

      try {
        Frame.StreamType streamType = Frame.StreamType.valueOf(bufferedSource.readByte());
        bufferedSource.skip(3);
        int frameSize = bufferedSource.readInt();

        return new Frame(streamType, bufferedSource.readByteArray(frameSize));
      }
      catch (EOFException e) {
        return null;
      }
      catch (IOException e) {
        log.error("error reading multiplexed frames", e);
        return null;
      }
    }
    else {
      long byteCount;
//      buffer.clear();
      try {
        byteCount = bufferedSource.read(buffer, 8192L);
        if (byteCount < 0) {
          return null;
        }
        return new Frame(Frame.StreamType.RAW, buffer.readByteArray(byteCount));
      }
      catch (IOException e) {
        return null;
      }
    }
  }

  @Override
  public boolean hasNext() {
    try {
      return !Thread.currentThread().isInterrupted() && bufferedSource.isOpen() && !bufferedSource.peek().exhausted();
    }
    catch (Exception e) {
      return false;
    }
  }
}
