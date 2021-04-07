package de.gesellix.docker.response;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonReader;
import com.squareup.moshi.JsonWriter;

import java.io.IOException;
import java.math.BigDecimal;

public class NumberToBigDecimalJsonAdapter extends JsonAdapter<Object> {

  private final JsonAdapter<Object> delegate;

  public NumberToBigDecimalJsonAdapter(JsonAdapter<Object> delegate) {
    this.delegate = delegate;
  }

  @Override
  public Object fromJson(JsonReader reader) throws IOException {
    if (!reader.peek().equals(JsonReader.Token.NUMBER)) {
      return delegate.fromJson(reader);
    }

    // allows Integer or Long values instead of strictly using Double as value type.
    return new BigDecimal(reader.nextString());
  }

  @Override
  public void toJson(JsonWriter writer, Object value) {
    throw new UnsupportedOperationException("not implemented");
  }
}
