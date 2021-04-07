package de.gesellix.docker.rawstream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.EOFException;
import java.io.IOException;

/**
 * See the paragraph _Stream format_ at https://docs.docker.com/engine/api/v1.33/#operation/ContainerAttach.
 * Reference implementation: https://github.com/moby/moby/blob/master/pkg/stdcopy/stdcopy.go.
 * Docker client GoDoc: https://godoc.org/github.com/moby/moby/client#Client.ContainerAttach.
 */
public class RawStreamHeader {

  private static final Logger log = LoggerFactory.getLogger(RawStreamHeader.class);

  public static final RawStreamHeader EMPTY_HEADER = new RawStreamHeader();
  private StreamType streamType;
  private int frameSize;

  private RawStreamHeader() {
  }

  public RawStreamHeader(final int[] header) throws IOException {
    if (header == null || header.length != 8) {
      throw new IllegalArgumentException("needs a header with a length of 8, got " + (header == null ? null : header.length) + ".");
    }

    // header[0] is the streamType
    streamType = readDockerStreamType(header);
    // header[1-3] will be ignored, since they're currently not used
    // header[4-7] is the frameSize
    frameSize = readFrameSize(header);
  }

  public StreamType readDockerStreamType(final int[] header) throws IOException {
    if (header[0] < 0) {
      throw new EOFException(String.valueOf(header[0]));
    }

    try {
      return StreamType.valueOf((byte) (header[0] & 0xFF));
    }
    catch (Exception e) {
      log.error("Invalid StreamType '" + String.valueOf((byte) (header[0] & 0xFF)) + "'");
      throw e;
    }
  }

  public int readFrameSize(int[] header) throws IOException {
    int ch1 = header[4];
    int ch2 = header[5];
    int ch3 = header[6];
    int ch4 = header[7];
    if ((ch1 | ch2 | ch3 | ch4) < 0) {
      throw new EOFException(ch1 + " " + ch2 + " " + ch3 + " " + ch4);
    }

    return (ch1 << 24) + (ch2 << 16) + (ch3 << 8) + (ch4 << 0);
  }

  public StreamType getStreamType() {
    return streamType;
  }

  public int getFrameSize() {
    return frameSize;
  }

  @Override
  public String toString() {
    return "RawDockerHeader{streamType=" + getStreamType() + ", frameSize=" + getFrameSize() + "}";
  }
}
