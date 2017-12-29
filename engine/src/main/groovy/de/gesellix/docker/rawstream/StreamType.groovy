package de.gesellix.docker.rawstream

/**
 <p>
 STREAM_TYPE can be:
 <ul>
 <li>0: stdin (will be written on stdout)</li>
 <li>1: stdout</li>
 <li>2: stderr</li>
 <li>3: systemerr</li>
 </ul>
 </p>
 See the paragraph _Stream format_ at https://docs.docker.com/engine/api/v1.33/#operation/ContainerAttach.
 <br/>
 Reference implementation: https://github.com/moby/moby/blob/master/pkg/stdcopy/stdcopy.go.
 <br/>
 Docker client GoDoc: https://godoc.org/github.com/moby/moby/client#Client.ContainerAttach.
 */
enum StreamType {

    STDIN((byte) 0),
    STDOUT((byte) 1),
    STDERR((byte) 2),
    SYSTEMERR((byte) 3)

    private final byte streamTypeId

    StreamType(streamTypeId) {
        this.streamTypeId = streamTypeId
    }

    static valueOf(byte b) {
        def value = values().find {
            return it.streamTypeId == b
        }
        if (!value) {
            throw new IllegalArgumentException("no enum value for ${b} found.")
        }
        return value
    }

    byte getStreamTypeId() {
        return streamTypeId
    }
}
