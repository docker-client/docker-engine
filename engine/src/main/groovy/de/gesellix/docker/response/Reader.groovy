package de.gesellix.docker.response

interface Reader {

  Object readNext()

  boolean hasNext()
}
