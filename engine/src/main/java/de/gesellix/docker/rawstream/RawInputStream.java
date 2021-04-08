package de.gesellix.docker.rawstream;

import de.gesellix.util.IOUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * See the paragraph _Stream format_ at https://docs.docker.com/engine/api/v1.33/#operation/ContainerAttach.
 * Reference implementation: https://github.com/moby/moby/blob/master/pkg/stdcopy/stdcopy.go.
 * Docker client GoDoc: https://godoc.org/github.com/moby/moby/client#Client.ContainerAttach.
 */
public class RawInputStream extends FilterInputStream {

  private static final Logger log = LoggerFactory.getLogger(RawInputStream.class);

  private static final int EOF = -1;

  private boolean multiplexStreams = true;
  private int remainingFrameSize = -1;

  public RawInputStream(InputStream inputStream) {
    super(inputStream);
  }

  public long copyFullyMultiplexed(OutputStream stdout) throws IOException {
    return copyFullyMultiplexed(stdout, null);
  }

  public long copyFullyMultiplexed(OutputStream stdout, OutputStream stderr) throws IOException {
    if (!(stdout != null || stderr != null)) {
      throw new IllegalArgumentException("need at least one of stdout or stderr");
    }

    if (!multiplexStreams) {
      OutputStream actualOutputStream = stdout != null ? stdout : stderr;
      return IOUtils.copy(super.in, actualOutputStream);
    }

    long sum = 0;
    int count;
    while (EOF != (count = copyFrame(stdout, stderr))) {
      sum += count;
    }
    return sum;
  }

  public int copyFrame(OutputStream stdout, OutputStream stderr) throws IOException {
    ByteArrayOutputStream systemerr = new ByteArrayOutputStream();

    Map<StreamType, OutputStream> outputStreamsByStreamType = new LinkedHashMap<>();
    outputStreamsByStreamType.put(StreamType.STDOUT, stdout != null ? stdout : stderr);
    outputStreamsByStreamType.put(StreamType.STDERR, stderr != null ? stderr : stdout);
    outputStreamsByStreamType.put(StreamType.SYSTEMERR, systemerr);

    RawStreamHeader parsedHeader = readFrameHeader();
    log.trace(parsedHeader.toString());
    if (parsedHeader.equals(RawStreamHeader.EMPTY_HEADER)) {
      return EOF;
    }

    int bytesToRead = parsedHeader.getFrameSize();
    final int DEFAULT_BUFFER_SIZE = 1024 * 4;
    byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
    int count = 0;
    int n;
    while (EOF != (n = super.read(buffer, 0, Math.min(DEFAULT_BUFFER_SIZE, bytesToRead)))) {
      OutputStream outputStream = outputStreamsByStreamType.get(parsedHeader.getStreamType());
      outputStream.write(buffer, 0, n);
      count += n;
      bytesToRead -= n;
      if (bytesToRead <= 0) {
        failOnSystemerr(parsedHeader, systemerr);
        return count;
      }
    }

    failOnSystemerr(parsedHeader, systemerr);
    return count;
  }

  private void failOnSystemerr(RawStreamHeader parsedHeader, final ByteArrayOutputStream systemerr) {
    if (parsedHeader.getStreamType().equals(StreamType.SYSTEMERR)) {
      log.error(systemerr.toString());
      throw new IllegalStateException("error from daemon in stream: " + systemerr);
    }
  }

  @Override
  public synchronized int read(@NotNull byte[] b, int off, int len) throws IOException {
    if (multiplexStreams) {
      if (remainingFrameSize <= 0) {
        RawStreamHeader parsedHeader = readFrameHeader();
        log.trace(parsedHeader.toString());
        if (parsedHeader.equals(RawStreamHeader.EMPTY_HEADER)) {
          return EOF;
        }

        remainingFrameSize = parsedHeader.getFrameSize();
      }

      int count = readRemainingFrameSize(b, off, len, remainingFrameSize);
      remainingFrameSize -= (Math.max(count, 0));
      return count;
    }

    return super.read(b, off, len);
  }

  public RawStreamHeader readFrameHeader() throws IOException {
    int[] headerBuf = new int[] {read(), read(), read(), read(),
                                 read(), read(), read(), read()};

//    log.trace("header bytes: '${headerBuf}'")
//    byte[] headerBufAsBytes = [
//        (byte) headerBuf[0], (byte) headerBuf[1], (byte) headerBuf[2], (byte) headerBuf[3],
//        (byte) headerBuf[4], (byte) headerBuf[5], (byte) headerBuf[6], (byte) headerBuf[7]]
//    log.trace("header bytes as String: '${new String(headerBufAsBytes)}'")

//    log.trace("read header: ${headerBuf}")
    if (Arrays.stream(headerBuf).anyMatch((it) -> it < 0)) {
      return RawStreamHeader.EMPTY_HEADER;
    }

    try {
      RawStreamHeader parsedHeader = new RawStreamHeader(headerBuf);
      log.trace(parsedHeader.toString());
      return parsedHeader;
    }
    catch (Exception e) {
      log.error("could not parse header - setting multiplexStreams=false could help.", e);
      throw e;
    }
  }

  public int readRemainingFrameSize(byte[] b, int off, int len, int remainingFrameSize) throws IOException {
    int updatedLen = Math.min(len, remainingFrameSize);
    return super.read(b, off, updatedLen);
  }

  public void setMultiplexStreams(boolean multiplexStreams) {
    this.multiplexStreams = multiplexStreams;
  }
}
