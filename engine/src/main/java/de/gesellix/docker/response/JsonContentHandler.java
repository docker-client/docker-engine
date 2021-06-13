package de.gesellix.docker.response;

import okio.Okio;
import okio.Source;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class JsonContentHandler {

  public Object getContent(InputStream stream) throws IOException {
    return readJsonObject(stream);
  }

  public Object getContent(Source source) throws IOException {
    return readJsonObject(source);
  }

  private Object readJsonObject(InputStream stream) throws IOException {
    Source source = Okio.source(stream);
    return readJsonObject(source);
  }

  private Object readJsonObject(Source source) throws IOException {
    List<Object> parsed = new ArrayList<>();
    JsonChunksReader<Object> reader = new JsonChunksReader<>(source);
    while (reader.hasNext()) {
      parsed.add(reader.readNext(Object.class));
    }

    if (parsed.size() == 1) {
      return parsed.get(0);
    }
    return parsed;
  }
}
