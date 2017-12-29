package de.gesellix.docker.rawstream

import groovy.util.logging.Slf4j

/**
 See the paragraph _Stream format_ at https://docs.docker.com/engine/api/v1.33/#operation/ContainerAttach.
 <br/>
 Reference implementation: https://github.com/moby/moby/blob/master/pkg/stdcopy/stdcopy.go.
 <br/>
 Docker client GoDoc: https://godoc.org/github.com/moby/moby/client#Client.ContainerAttach.
 */
@Slf4j
class RawStreamHeader {

    final static EMPTY_HEADER = new RawStreamHeader()

    StreamType streamType
    int frameSize

    private RawStreamHeader() {
    }

    RawStreamHeader(int[] header) {
        if (header == null || header.length != 8) {
            throw new IllegalArgumentException("needs a header with a length of 8, got ${header?.length}.")
        }

        // header[0] is the streamType
        streamType = readDockerStreamType(header)
        // header[1-3] will be ignored, since they're currently not used
        // header[4-7] is the frameSize
        frameSize = readFrameSize(header)
    }

    StreamType readDockerStreamType(int[] header) throws IOException {
        if (header[0] < 0) {
            throw new EOFException("${header[0]}")
        }
        try {
            return StreamType.valueOf((byte) (header[0] & 0xFF))
        }
        catch (Exception e) {
            log.error("Invalid StreamType '${(byte) (header[0] & 0xFF)}'")
            throw e
        }
    }

    int readFrameSize(int[] header) throws IOException {
        int ch1 = header[4]
        int ch2 = header[5]
        int ch3 = header[6]
        int ch4 = header[7]
        if ((ch1 | ch2 | ch3 | ch4) < 0) {
            throw new EOFException("$ch1 $ch2 $ch3 $ch4")
        }
        return (ch1 << 24) + (ch2 << 16) + (ch3 << 8) + (ch4 << 0)
    }

    @Override
    String toString() {
        return "RawDockerHeader{streamType=${streamType}, frameSize=${frameSize}}"
    }
}
