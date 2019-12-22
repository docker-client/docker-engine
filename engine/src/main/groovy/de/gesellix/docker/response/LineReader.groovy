package de.gesellix.docker.response

import okio.BufferedSource
import okio.Okio
import okio.Source

class LineReader implements Reader {

    private BufferedSource buffer

    LineReader(Source source) {
        this.buffer = Okio.buffer(source)
    }

    Object readNext() {
        return buffer.readUtf8Line()
    }

    boolean hasNext() {
        !Thread.currentThread().isInterrupted() && !buffer.exhausted()
    }
}
