package de.gesellix.docker.engine

import groovy.transform.ToString

import java.util.concurrent.Future

@ToString(includeNames = true)
class EngineResponse {

  EngineResponseStatus status = new EngineResponseStatus()
  def headers
  String contentType
  String mimeType
  String contentLength
  InputStream stream
  def content

  Future taskFuture

  OkResponseCallback responseCallback
}
