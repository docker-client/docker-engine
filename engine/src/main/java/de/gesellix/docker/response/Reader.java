package de.gesellix.docker.response;

import java.io.IOException;

public interface Reader {

  Object readNext() throws IOException;

  boolean hasNext() throws IOException;
}
