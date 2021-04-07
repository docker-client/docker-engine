package de.gesellix.docker.response;

import com.squareup.moshi.JsonReader;
import com.squareup.moshi.Moshi;
import okio.Okio;
import okio.Source;

import java.io.IOException;

public class JsonChunksReader implements Reader {

  private final JsonReader reader;
  private final Moshi moshi;

  public JsonChunksReader(Source source) {
    moshi = new Moshi.Builder().add(new CustomObjectAdapterFactory()).build();
    reader = JsonReader.of(Okio.buffer(source));

    // For transfer-encoding: chunked:
    // allows repeated `readNext` calls to consume
    // a complete stream of JSON chunks (delimited or not).
    reader.setLenient(true);
  }

  @Override
  public Object readNext() throws IOException {
    return moshi.adapter(Object.class).fromJson(reader);
//    return reader.readJsonValue();
  }

  @Override
  public boolean hasNext() throws IOException {
    return !Thread.currentThread().isInterrupted() && reader.hasNext();
  }
}
