package de.gesellix.docker.response

import okio.Okio
import okio.Source

class JsonContentHandler {

    Object getContent(InputStream stream) throws IOException {
        return readJsonObject(stream)
    }

    Object getContent(Source source) throws IOException {
        return readJsonObject(source)
    }

    private Object readJsonObject(InputStream stream) {
        Source source = Okio.source(stream)
        return readJsonObject(source)
    }

    private Object readJsonObject(Source source) {
        def parsed = []
        def reader = new JsonChunksReader(source)
        while (reader.hasNext()) {
            parsed << reader.readNext()
        }
        if (parsed.size() == 1) {
            return parsed.first()
        }
        return parsed
    }
}
