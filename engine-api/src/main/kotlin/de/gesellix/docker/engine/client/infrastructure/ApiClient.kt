package de.gesellix.docker.engine.client.infrastructure

import de.gesellix.docker.client.filesocket.NamedPipeSocket
import de.gesellix.docker.client.filesocket.NamedPipeSocketFactory
import de.gesellix.docker.client.filesocket.UnixSocket
import de.gesellix.docker.client.filesocket.UnixSocketFactory
import de.gesellix.docker.client.filesocket.UnixSocketFactorySupport
import de.gesellix.docker.engine.DockerClientConfig
import de.gesellix.docker.engine.EngineClient
import de.gesellix.docker.engine.EngineRequest
import de.gesellix.docker.engine.OkDockerClient
import de.gesellix.docker.engine.RequestMethod.DELETE
import de.gesellix.docker.engine.RequestMethod.GET
import de.gesellix.docker.engine.RequestMethod.HEAD
import de.gesellix.docker.engine.RequestMethod.OPTIONS
import de.gesellix.docker.engine.RequestMethod.PATCH
import de.gesellix.docker.engine.RequestMethod.POST
import de.gesellix.docker.engine.RequestMethod.PUT
import de.gesellix.docker.engine.RequestMethod.valueOf
import de.gesellix.docker.ssl.SslSocketConfigFactory
import okhttp3.HttpUrl
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.nio.file.Files
import java.util.concurrent.TimeUnit

open class ApiClient(val baseUrl: String, val dockerClientConfig: DockerClientConfig = DockerClientConfig()) {
  companion object {

    protected const val ContentType = "Content-Type"
    protected const val Accept = "Accept"
    protected const val Authorization = "Authorization"
    protected const val TextPlainMediaType = "text/plain"
    protected const val JsonMediaType = "application/json"
    protected const val OctetStreamMediaType = "application/octet-stream"

    val apiKey: MutableMap<String, String> = mutableMapOf()
    val apiKeyPrefix: MutableMap<String, String> = mutableMapOf()
    var username: String? = null
    var password: String? = null
    var accessToken: String? = null

    val socketFactories: MutableMap<String, (OkHttpClient.Builder) -> OkHttpClient.Builder> = mutableMapOf()

    @JvmStatic
    val engineClient: EngineClient by lazy {
      OkDockerClient()
    }

    @JvmStatic
    val client: OkHttpClient by lazy {
      builder.build()
    }

    @JvmStatic
    val builder: OkHttpClient.Builder = OkHttpClient.Builder()
  }

  protected inline fun <reified T> requestBody(content: T, mediaType: String = JsonMediaType): RequestBody =
    when {
      content is File -> content.asRequestBody(
        mediaType.toMediaTypeOrNull()
      )
      mediaType == JsonMediaType -> Serializer.moshi.adapter(T::class.java).toJson(content).toRequestBody(
        mediaType.toMediaTypeOrNull()
      )
//      var  source: Source?= body as InputStream?. source ()
//      var  buffer: BufferedSource?= source.buffer()
//      try {
//        requestBody = RequestBody.create(buffer.readByteArray(), parse.parse(contentType))
//      } catch (e: IOException) {
//        OkDockerClient.log.error("Failed to read request body", e)
//        throw RuntimeException("Failed to read request body", e)
//      }
// See https://github.com/square/okhttp/pull/3912 for a possible implementation
// Would we have issues with non-closed InputStreams?
// More details/examples:
// - https://github.com/minio/minio-java/issues/924
// - https://github.com/square/okhttp/issues/2424
//      mediaType == OctetStreamMediaType && content is InputStream -> RequestBody.create(content.source().buffer(), mediaType.toMediaTypeOrNull())
      else -> throw UnsupportedOperationException("requestBody only supports JSON body and File body, not $mediaType.")
    }

  protected inline fun <reified T : Any?> responseBody(body: ResponseBody?, mediaType: String? = JsonMediaType): T? {
    if (body == null) {
      return null
    }
    val bodyContent = body.string()
//    if (bodyContent.isEmpty()) {
//      return null
//    }
    if (T::class.java == File::class.java) {
      // return tempfile
      val f = Files.createTempFile("tmp.de.gesellix.docker.client", null).toFile()
      f.deleteOnExit()
      val out = BufferedWriter(FileWriter(f))
      out.write(bodyContent)
      out.close()
      return f as T
    }
    return when (mediaType) {
      JsonMediaType -> Serializer.moshi.adapter(T::class.java).fromJson(bodyContent)
      TextPlainMediaType -> bodyContent as T
      else -> throw UnsupportedOperationException("responseBody currently only supports JSON body, not $mediaType.")
    }
  }

  protected inline fun <reified T : Any?> request(requestConfig: RequestConfig): ApiInfrastructureResponse<T?> {
    return request<T>(EngineRequest(valueOf(requestConfig.method.name), requestConfig.path).also {
      it.headers = requestConfig.headers
      it.query = requestConfig.query
      it.body = requestConfig.body
    })
  }

  protected inline fun <reified T : Any?> request(requestConfig: EngineRequest): ApiInfrastructureResponse<T?> {
    val httpUrl = buildHttpUrl().build()

    val pathWithOptionalApiVersion = when {
      requestConfig.apiVersion != null -> {
        requestConfig.apiVersion + "/" + requestConfig.path
      }
      else -> {
        requestConfig.path
      }
    }

    val url = httpUrl.newBuilder()
      .addPathSegments(pathWithOptionalApiVersion.trimStart('/'))
      .apply {
        requestConfig.query.forEach { query ->
          query.value.forEach { queryValue ->
            addQueryParameter(query.key, queryValue)
          }
        }
      }.build()

    // take content-type/accept from spec or set to default (application/json) if not defined
    if (requestConfig.headers[ContentType].isNullOrEmpty()) {
      requestConfig.headers[ContentType] = JsonMediaType
    }
    if (requestConfig.headers[Accept].isNullOrEmpty()) {
      requestConfig.headers[Accept] = JsonMediaType
    }
    val headers = requestConfig.headers

    if (headers[ContentType] ?: "" == "") {
      throw kotlin.IllegalStateException("Missing Content-Type header. This is required.")
    }

    if (headers[Accept] ?: "" == "") {
      throw kotlin.IllegalStateException("Missing Accept header. This is required.")
    }

    // TODO: support multiple contentType options here.
    val contentType = (headers[ContentType] as String).substringBefore(";").toLowerCase()

    val request = when (requestConfig.method) {
      DELETE -> Request.Builder().url(url).delete(requestBody(requestConfig.body, contentType))
      GET -> Request.Builder().url(url)
      HEAD -> Request.Builder().url(url).head()
      PATCH -> Request.Builder().url(url).patch(requestBody(requestConfig.body, contentType))
      PUT -> Request.Builder().url(url).put(requestBody(requestConfig.body, contentType))
      POST -> Request.Builder().url(url).post(requestBody(requestConfig.body, contentType))
      OPTIONS -> Request.Builder().url(url).method("OPTIONS", null)
      null -> throw kotlin.IllegalStateException("Request method is null")
    }.apply {
      headers.forEach { header -> addHeader(header.key, header.value) }
    }.build()

//    val engineResponse = engineClient.request(requestConfig)
    val actualClient = buildHttpClient(client.newBuilder())
//      .proxy(proxy) // TODO
      // do we need to disable the timeout for streaming?
      .connectTimeout(requestConfig.timeout.toLong(), TimeUnit.MILLISECONDS)
      .readTimeout(requestConfig.timeout.toLong(), TimeUnit.MILLISECONDS)
      .build()

    val response = actualClient.newCall(request).execute()
    val accept = response.header(ContentType)?.substringBefore(";")?.toLowerCase()

    // TODO: handle specific mapping types. e.g. Map<int, Class<?>>
    when {
      response.isRedirect -> return Redirection(
        response.code,
        response.headers.toMultimap()
      )
      response.isInformational -> return Informational(
        response.message,
        response.code,
        response.headers.toMultimap()
      )
      response.isSuccessful -> return Success(
        responseBody(response.body, accept),
        response.code,
        response.headers.toMultimap()
      )
      response.isClientError -> return ClientError(
        response.message,
        response.body?.string(),
        response.code,
        response.headers.toMultimap()
      )
      else -> return ServerError(
        response.message,
        response.body?.string(),
        response.code,
        response.headers.toMultimap()
      )
    }
  }

  open fun buildHttpUrl(): HttpUrl.Builder {
//    baseUrl.toHttpUrlOrNull() ?: throw IllegalStateException("baseUrl is invalid.")
    return when (dockerClientConfig.scheme) {
      "unix" -> HttpUrl.Builder()
        .scheme("http")
        .host(UnixSocket().encodeHostname(dockerClientConfig.host))
      //                    .port(/not/allowed/for/unix/socket/)
      "npipe" -> HttpUrl.Builder()
        .scheme("http")
        .host(NamedPipeSocket().encodeHostname(dockerClientConfig.host))
      //                    .port(/not/allowed/for/npipe/socket/)
      else -> HttpUrl.Builder()
        .scheme(dockerClientConfig.scheme)
        .host(dockerClientConfig.host)
        .port(dockerClientConfig.port)
    }
  }

  open fun buildHttpClient(builder: OkHttpClient.Builder): OkHttpClient.Builder {
    val protocol = dockerClientConfig.scheme
    val foo = socketFactories[protocol]
    if (foo != null) {
      return foo(builder)
    }
    throw IllegalStateException("$protocol socket not supported.")
  }

  init {
    if (UnixSocketFactorySupport().isSupported) {
      socketFactories["unix"] = { builder ->
        val factory = UnixSocketFactory()
        builder
          .socketFactory(factory)
          .dns(factory)
      }
    }
    socketFactories["npipe"] = { builder ->
      val factory = NamedPipeSocketFactory()
      builder
        .socketFactory(factory)
        .dns(factory)
    }
    socketFactories["https"] = { builder ->
      val dockerSslSocket = SslSocketConfigFactory().createDockerSslSocket(dockerClientConfig.certPath)
      builder
        .sslSocketFactory(dockerSslSocket.sslSocketFactory, dockerSslSocket.trustManager)
    }
  }
}
