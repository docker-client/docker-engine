package de.gesellix.docker.response

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi

import java.lang.annotation.Annotation
import java.lang.reflect.Type

class CustomObjectAdapterFactory implements JsonAdapter.Factory {

    @Override
    JsonAdapter<?> create(Type type, Set<? extends Annotation> annotations, Moshi moshi) {

        if (type != Object) {
            return null
        }
        def delegate = moshi.nextAdapter(this, Object, annotations)
        return new NumberToBigDecimalJsonAdapter(delegate)
    }
}
