package de.gesellix.docker.response

import com.squareup.moshi.JsonReader
import com.squareup.moshi.Moshi
import okio.Okio
import okio.Source

class JsonChunksReader implements Reader {

    JsonReader reader
    Moshi moshi

    JsonChunksReader(Source source) {
        moshi = new Moshi.Builder()
                .add(new CustomObjectAdapterFactory())
                .build()
        reader = JsonReader.of(Okio.buffer((Source) source))

        // For transfer-encoding: chunked:
        // allows repeated `readNext` calls to consume
        // a complete stream of JSON chunks (delimited or not).
        reader.setLenient(true)
    }

    Object readNext() {
        return moshi.adapter(Object).fromJson(reader)
//        return reader.readJsonValue()
    }

    boolean hasNext() {
        return !Thread.currentThread().isInterrupted() && reader.hasNext()
    }
}
