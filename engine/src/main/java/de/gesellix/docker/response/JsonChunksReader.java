package de.gesellix.docker.response;

import com.squareup.moshi.JsonReader;
import com.squareup.moshi.Moshi;
import de.gesellix.docker.json.CustomObjectAdapterFactory;
import okio.Okio;
import okio.Source;

import java.io.IOException;

public class JsonChunksReader<T> implements Reader<T> {

  private final JsonReader reader;
  private final Moshi moshi;

  public JsonChunksReader(Source source) {
    this(source, new Moshi.Builder().add(new CustomObjectAdapterFactory()).build());
  }

  public JsonChunksReader(Source source, Moshi moshi) {
    this.moshi = moshi;
    this.reader = JsonReader.of(Okio.buffer(source));
    // For transfer-encoding: chunked:
    // allows repeated `readNext` calls to consume
    // a complete stream of JSON chunks (delimited or not).
    this.reader.setLenient(true);
  }

  @Override
  public T readNext(Class<T> type) throws IOException {
    return moshi.adapter(type).fromJson(reader);
//    return reader.readJsonValue();
  }

  @Override
  public boolean hasNext() throws IOException {
    return !Thread.currentThread().isInterrupted() && reader.hasNext();
  }
}
