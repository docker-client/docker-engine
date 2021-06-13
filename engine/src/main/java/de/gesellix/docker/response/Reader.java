package de.gesellix.docker.response;

import java.io.IOException;

public interface Reader<T> {

  T readNext(Class<T> type) throws IOException;

  boolean hasNext() throws IOException;
}
