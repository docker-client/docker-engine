package de.gesellix.docker.response;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Set;

public class CustomObjectAdapterFactory implements JsonAdapter.Factory {

  @Override
  public JsonAdapter<?> create(Type type, Set<? extends Annotation> annotations, Moshi moshi) {

    if (!type.equals(Object.class)) {
      return null;
    }

    JsonAdapter<Object> delegate = moshi.nextAdapter(this, Object.class, annotations);
    return new NumberToBigDecimalJsonAdapter(delegate);
  }
}
