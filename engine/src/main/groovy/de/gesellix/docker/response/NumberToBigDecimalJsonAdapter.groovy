package de.gesellix.docker.response

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter

class NumberToBigDecimalJsonAdapter extends JsonAdapter<Object> {

  private JsonAdapter<Object> delegate

  NumberToBigDecimalJsonAdapter(JsonAdapter<Object> delegate) {
    this.delegate = delegate
  }

  @Override
  Object fromJson(JsonReader reader) throws IOException {
    if (reader.peek() != JsonReader.Token.NUMBER) {
      return delegate.fromJson(reader)
    }
    // allows Integer or Long values instead of strictly using Double as value type.
    return new BigDecimal(reader.nextString())
  }

  @Override
  void toJson(JsonWriter writer, /*@javax.annotation.Nullable*/ Object value) throws IOException {
    throw new UnsupportedOperationException("not implemented")
  }
}
