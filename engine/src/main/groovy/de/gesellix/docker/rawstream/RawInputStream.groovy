package de.gesellix.docker.rawstream

import de.gesellix.util.IOUtils
import groovy.util.logging.Slf4j

/**
 See the paragraph _Stream format_ at https://docs.docker.com/engine/api/v1.33/#operation/ContainerAttach.
 <br/>
 Reference implementation: https://github.com/moby/moby/blob/master/pkg/stdcopy/stdcopy.go.
 <br/>
 Docker client GoDoc: https://godoc.org/github.com/moby/moby/client#Client.ContainerAttach.
 */
@Slf4j
class RawInputStream extends FilterInputStream {

    final static int EOF = -1

    RawInputStream(InputStream inputStream) {
        super(inputStream)
    }

    def multiplexStreams = true
    def remainingFrameSize = -1

    def copyFullyMultiplexed(OutputStream stdout, OutputStream stderr = null) {
        if (!(stdout || stderr)) {
            throw new IllegalArgumentException("need at least one of stdout or stderr")
        }

        if (!multiplexStreams) {
            def actualOutputStream = stdout ?: stderr
            return IOUtils.copy(super.in, actualOutputStream)
        }

        int sum = 0
        int count
        while (EOF != (count = copyFrame(stdout, stderr))) {
            sum += count
        }
        return sum
    }

    int copyFrame(OutputStream stdout, OutputStream stderr) {
        def systemerr = new ByteArrayOutputStream()

        Map<StreamType, OutputStream> outputStreamsByStreamType = [:]
        outputStreamsByStreamType[StreamType.STDOUT] = stdout ?: stderr
        outputStreamsByStreamType[StreamType.STDERR] = stderr ?: stdout
        outputStreamsByStreamType[StreamType.SYSTEMERR] = systemerr

        def parsedHeader = readFrameHeader()
        log.trace(parsedHeader.toString())
        if (parsedHeader == RawStreamHeader.EMPTY_HEADER) {
            return EOF
        }

        int bytesToRead = parsedHeader.frameSize
        final int DEFAULT_BUFFER_SIZE = 1024 * 4
        def buffer = new byte[DEFAULT_BUFFER_SIZE]
        long count = 0
        int n
        while (EOF != (n = super.read(buffer, 0, Math.min(DEFAULT_BUFFER_SIZE, bytesToRead)))) {
            def outputStream = outputStreamsByStreamType[parsedHeader.streamType]
            outputStream.write(buffer, 0, n)
            count += n
            bytesToRead -= n
            if (bytesToRead <= 0) {
                failOnSystemerr(parsedHeader, systemerr)
                return count
            }
        }

        failOnSystemerr(parsedHeader, systemerr)
        return count
    }

    private void failOnSystemerr(RawStreamHeader parsedHeader, ByteArrayOutputStream systemerr) {
        if (parsedHeader.streamType == StreamType.SYSTEMERR) {
            log.error(new String(systemerr.toByteArray()))
            throw new IllegalStateException("error from daemon in stream: ${new String(systemerr.toByteArray())}")
        }
    }

    @Override
    synchronized int read(byte[] b, int off, int len) throws IOException {
        if (multiplexStreams) {
            if (remainingFrameSize <= 0) {
                def parsedHeader = readFrameHeader()
                log.trace(parsedHeader.toString())
                if (parsedHeader == RawStreamHeader.EMPTY_HEADER) {
                    return EOF
                }
                remainingFrameSize = parsedHeader.frameSize
            }
            def count = readRemainingFrameSize(b, off, len, remainingFrameSize)
            remainingFrameSize -= (count >= 0 ? count : 0)
            return count
        }
        return super.read(b, off, len)
    }

    def readFrameHeader() {
        int[] headerBuf = [
                read(), read(), read(), read(),
                read(), read(), read(), read()]

//    log.trace("header bytes: '${headerBuf}'")
//    byte[] headerBufAsBytes = [
//        (byte) headerBuf[0], (byte) headerBuf[1], (byte) headerBuf[2], (byte) headerBuf[3],
//        (byte) headerBuf[4], (byte) headerBuf[5], (byte) headerBuf[6], (byte) headerBuf[7]]
//    log.trace("header bytes as String: '${new String(headerBufAsBytes)}'")

//    log.trace("read header: ${headerBuf}")
        if (headerBuf.find { it < 0 }) {
            return RawStreamHeader.EMPTY_HEADER
        }

        try {
            def parsedHeader = new RawStreamHeader(headerBuf)
            log.trace(parsedHeader.toString())
            return parsedHeader
        }
        catch (Exception e) {
            log.error("could not parse header - setting multiplexStreams=false could help.", e)
            throw e
        }
    }

    def readRemainingFrameSize(byte[] b, int off, int len, int remainingFrameSize) {
        def updatedLen = Math.min(len, remainingFrameSize)
        return super.read(b, off, updatedLen)
    }

    static class NullOutputStream extends OutputStream {
        static final NullOutputStream INSTANCE = new NullOutputStream()

        private NullOutputStream() {}

        void write(int b) throws IOException {
            throw new IOException("Stream closed")
        }
    }
}
