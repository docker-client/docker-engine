package de.gesellix.docker.engine;

import com.squareup.moshi.Moshi;
import de.gesellix.docker.client.filesocket.FileSocketFactory;
import de.gesellix.docker.client.filesocket.HostnameEncoder;
import de.gesellix.docker.client.filesocket.NamedPipeSocketFactory;
import de.gesellix.docker.client.filesocket.UnixSocketFactory;
import de.gesellix.docker.client.filesocket.UnixSocketFactorySupport;
import de.gesellix.docker.json.CustomObjectAdapterFactory;
import de.gesellix.docker.rawstream.RawInputStream;
import de.gesellix.docker.response.JsonContentHandler;
import de.gesellix.docker.ssl.DockerSslSocket;
import de.gesellix.docker.ssl.SslSocketConfigFactory;
import de.gesellix.util.IOUtils;
import okhttp3.CacheControl;
import okhttp3.Call;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okhttp3.internal.http.HttpMethod;
import okio.BufferedSource;
import okio.Okio;
import okio.Source;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Proxy;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static de.gesellix.docker.client.filesocket.FileSocket.SOCKET_MARKER;
import static de.gesellix.docker.engine.RequestMethod.DELETE;
import static de.gesellix.docker.engine.RequestMethod.GET;
import static de.gesellix.docker.engine.RequestMethod.HEAD;
import static de.gesellix.docker.engine.RequestMethod.POST;
import static de.gesellix.docker.engine.RequestMethod.PUT;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * Will be replaced with the implementation from <a href="https://github.com/docker-client/docker-remote-api-client">github.com/docker-client/docker-remote-api-client</a>.
 *
 * @deprecated
 */
@Deprecated
public class OkDockerClient implements EngineClient {

  private static final Logger log = LoggerFactory.getLogger(OkDockerClient.class);

  private final Map<String, Object> socketFactories = new LinkedHashMap<>();

  private final DockerClientConfig dockerClientConfig;
  private Proxy proxy;

  private final Moshi moshi;

  public OkDockerClient() {
    this(new DockerClientConfig());
  }

  public OkDockerClient(String dockerHost) {
    this(new DockerClientConfig(dockerHost));
  }

  public OkDockerClient(DockerClientConfig dockerClientConfig) {
    this(dockerClientConfig, Proxy.NO_PROXY);
  }

  public OkDockerClient(DockerClientConfig dockerClientConfig, Proxy proxy) {
    if (new UnixSocketFactorySupport().isSupported()) {
      socketFactories.put("unix", new UnixSocketFactory());
    }
    socketFactories.put("npipe", new NamedPipeSocketFactory());
    socketFactories.put("https", new SslSocketConfigFactory());

    this.dockerClientConfig = dockerClientConfig;
    this.proxy = proxy;

    this.moshi = new Moshi.Builder()
        .add(new CustomObjectAdapterFactory())
        .build();
  }

  @Override
  public EngineResponse head(Map<String, Object> requestConfig) {
    EngineRequest engineRequest = ensureValidRequestConfig(requestConfig, HEAD);
    return request(engineRequest);
  }

  @Override
  public EngineResponse get(Map<String, Object> requestConfig) {
    EngineRequest engineRequest = ensureValidRequestConfig(requestConfig, GET);
    return request(engineRequest);
  }

  @Override
  public EngineResponse put(Map<String, Object> requestConfig) {
    EngineRequest engineRequest = ensureValidRequestConfig(requestConfig, PUT);
    return request(engineRequest);
  }

  @Override
  public EngineResponse post(Map<String, Object> requestConfig) {
    EngineRequest engineRequest = ensureValidRequestConfig(requestConfig, POST);
    return request(engineRequest);
  }

  @Override
  public EngineResponse delete(Map<String, Object> requestConfig) {
    EngineRequest engineRequest = ensureValidRequestConfig(requestConfig, DELETE);
    return request(engineRequest);
  }

  @Override
  public WebSocket webSocket(Map<String, Object> requestConfig, WebSocketListener listener) {
    EngineRequest engineRequest = ensureValidRequestConfig(requestConfig, GET);
    Request.Builder requestBuilder = prepareRequest(new Request.Builder(), engineRequest);
    Request request = requestBuilder.build();

    OkHttpClient.Builder clientBuilder = prepareClient(new OkHttpClient.Builder(), engineRequest.getTimeout());
    OkHttpClient client = newClient(clientBuilder);

    return client.newWebSocket(request, listener);
  }

  @Override
  public EngineResponse request(EngineRequest requestConfig) {
    EngineRequest config = ensureValidRequestConfig(requestConfig);

    Request.Builder requestBuilder = prepareRequest(new Request.Builder(), config);
    final Request request = requestBuilder.build();

    OkHttpClient.Builder clientBuilder = prepareClient(new OkHttpClient.Builder(), config.getTimeout());
    final OkHttpClient client = newClient(clientBuilder);

    log.debug(request.method() + " " + request.url() + " using proxy: " + client.proxy());

    Call call = client.newCall(request);
    EngineResponse dockerResponse;
    try {
      Response response = call.execute();
      log.debug("response: " + response);
      dockerResponse = handleResponse(response, config);
      if (dockerResponse.getStream() == null) {
//          log.warn("closing response...");
        response.close();
      }
    }
    catch (Exception e) {
      log.error("Request failed", e);
      throw new RuntimeException("Request failed", e);
    }
    return dockerResponse;
  }

  private Request.Builder prepareRequest(final Request.Builder builder, final EngineRequest config) {
    String method = config.getMethod().name();
    String contentType = config.getContentType();
    Map<String, String> additionalHeaders = config.getHeaders();
    Object body = config.getBody();

    String protocol = dockerClientConfig.getScheme();
    String host = dockerClientConfig.getHost();
    int port = dockerClientConfig.getPort();

    String path = config.getPath();
    if (config.getApiVersion() != null) {
      path = config.getApiVersion() + "/" + path;
    }
    String queryAsString;
    if (config.getQuery() != null) {
      queryAsString = queryToString(config.getQuery());
    }
    else {
      queryAsString = "";
    }

    HttpUrl.Builder urlBuilder = new HttpUrl.Builder().addPathSegments(path);
    if (queryAsString != null && !queryAsString.isEmpty()) {
      urlBuilder = urlBuilder.encodedQuery(queryAsString);
//    } else {
//      urlBuilder = urlBuilder.encodedQuery(null);
    }
    HttpUrl httpUrl = createUrl(urlBuilder, protocol, host, port);

    RequestBody requestBody = createRequestBody(method, contentType, body);
    builder.method(method, requestBody).url(httpUrl).cacheControl(CacheControl.FORCE_NETWORK);
    if (additionalHeaders != null) {
      additionalHeaders.forEach(builder::header);
    }
    return builder;
  }

  private OkHttpClient.Builder prepareClient(OkHttpClient.Builder builder, int currentTimeout) {
    String protocol = dockerClientConfig.getScheme();
    switch (protocol) {
      case "unix":
        if (!socketFactories.containsKey(protocol)) {
          log.error("Unix domain socket not supported, but configured (using defaults?). Please consider changing the DOCKER_HOST environment setting to use tcp.");
          throw new IllegalStateException("Unix domain socket not supported.");
        }
        FileSocketFactory unixSocketFactory = (FileSocketFactory) socketFactories.get(protocol);
        builder
            .socketFactory(unixSocketFactory)
            .dns(unixSocketFactory)
            .build();
        break;
      case "npipe":
        FileSocketFactory npipeSocketFactory = (FileSocketFactory) socketFactories.get(protocol);
        builder
            .socketFactory(npipeSocketFactory)
            .dns(npipeSocketFactory)
            .build();
        break;
      case "https":
        String certPath = dockerClientConfig.getCertPath();
        SslSocketConfigFactory sslSocketFactory = (SslSocketConfigFactory) socketFactories.get(protocol);
        DockerSslSocket dockerSslSocket = sslSocketFactory.createDockerSslSocket(certPath);
        if (dockerSslSocket != null) {
          builder
              .sslSocketFactory(dockerSslSocket.getSslSocketFactory(), dockerSslSocket.getTrustManager())
              .build();
        }
        break;
    }
    builder.proxy(proxy);

    // do we need to disable the timeout for streaming?
    builder
        .connectTimeout(currentTimeout, MILLISECONDS)
        .readTimeout(currentTimeout, MILLISECONDS);
    return builder;
  }

  public OkHttpClient newClient(OkHttpClient.Builder clientBuilder) {
    return clientBuilder.build();
  }

  private HttpUrl createUrl(HttpUrl.Builder urlBuilder, String protocol, String host, int port) {
    HttpUrl httpUrl;
    switch (protocol) {
      case "unix":
      case "npipe":
        httpUrl = urlBuilder
            .scheme("http")
            .host(new HostnameEncoder().encode(host) + SOCKET_MARKER)
            .build();
        break;
      default:
        httpUrl = urlBuilder
            .scheme(protocol)
            .host(host)
            .port(port)
            .build();
        break;
    }
    return httpUrl;
  }

  private RequestBody createRequestBody(String method, String contentType, Object body) {
    if (body == null && HttpMethod.requiresRequestBody(method)) {
      return RequestBody.create("", MediaType.parse("application/json"));
    }

    RequestBody requestBody = null;
    if (body != null) {
      switch (contentType) {
        case "application/json":
          requestBody = RequestBody.create(moshi.adapter(Map.class).toJson((Map) body), MediaType.parse(contentType));
          break;
        case "application/octet-stream":
        default:
          Source source = Okio.source((InputStream) body);
          BufferedSource buffer = Okio.buffer(source);
          requestBody = new StreamingRequestBody(MediaType.parse(contentType), buffer);
          break;
      }
    }
    return requestBody;
  }

  public EngineResponse handleResponse(Response httpResponse, EngineRequest config) throws IOException {
    final EngineResponse response = readHeaders(httpResponse);

    if (response.getStatus().getCode() == 204) {
      if (response.getStream() != null) {
        // redirect the response body to /dev/null, since it's expected to be empty
        IOUtils.consumeToDevNull(response.getStream());
      }
      return response;
    }
    ResponseBody body = httpResponse.body();

    String mimeType = response.getMimeType();
    if (mimeType == null) {
      mimeType = "";
    }
    switch (mimeType) {
      case "application/vnd.docker.multiplexed-stream":
      case "application/vnd.docker.raw-stream":
        InputStream rawStream = new RawInputStream(body.byteStream());
        if (config.getStdout() != null) {
          log.debug("redirecting to stdout.");
          IOUtils.copy(rawStream, config.getStdout());
          response.setStream(null);
        }
        else {
          response.setStream(rawStream);
        }
        break;
      case "application/json":
        if (config.isAsync()) {
          consumeResponseBody(response, body.source(), config);
        }
        else {
          Object content = new JsonContentHandler().getContent(body.source());
          consumeResponseBody(response, content, config);
        }
        break;
      case "text/html":
      case "text/plain":
        InputStream text = body.byteStream();
        consumeResponseBody(response, text, config);
        break;
      case "application/octet-stream":
        InputStream octet = body.byteStream();
        if (config.getStdout() != null) {
          log.debug("redirecting to stdout.");
          IOUtils.copy(octet, config.getStdout());
          response.setStream(null);
        }
        else {
          log.debug("passing through via `response.stream`.");
          response.setStream(octet);
        }
        break;
      case "application/x-tar":
        if (response.getStream() != null) {
          if (config.getStdout() != null) {
            log.debug("redirecting to stdout.");
            IOUtils.copy(response.getStream(), config.getStdout());
            response.setStream(null);
          }
          else {
            log.info(response.getMimeType() + " stream won't be consumed, but is available in the response.");
          }
        }
        break;
      default:
        if (body == null || body.contentLength() == 0) {
          response.setContent(body == null ? null : body.string());
          response.setStream(null);
          return response;
        }
        log.debug("unexpected mime type '" + response.getMimeType() + "'.");
        if (body.contentLength() == -1) {
          InputStream stream = body.byteStream();
          if (config.getStdout() != null) {
            log.debug("redirecting to stdout.");
            IOUtils.copy(stream, config.getStdout());
            response.setStream(null);
          }
          else {
            log.debug("passing through via `response.stream`.");
            response.setStream(stream);
          }
        }
        else {
          log.debug("passing through via `response.content`.");
          response.setContent(body.string());
          response.setStream(null);
        }
        break;
    }

    return response;
  }

  private EngineResponse readHeaders(Response httpResponse) {
    final EngineResponse dockerResponse = new EngineResponse();

    EngineResponseStatus status = new EngineResponseStatus();
    status.setText(httpResponse.message());
    status.setCode(httpResponse.code());
    status.setSuccess(httpResponse.isSuccessful());
    dockerResponse.setStatus(status);
    log.trace("status: " + dockerResponse.getStatus());

    final Headers headers = httpResponse.headers();
    log.trace("headers: \n" + headers);
    dockerResponse.setHeaders(headers);

    String contentType = headers.get("content-type");
    dockerResponse.setContentType(contentType);

    String contentLength = headers.get("content-length");
    if (contentLength == null) {
      contentLength = "-1";
    }
    dockerResponse.setContentLength(contentLength);

    String mimeType = getMimeType(contentType);
    dockerResponse.setMimeType(mimeType);

    if (dockerResponse.getStatus().getSuccess()) {
      dockerResponse.setStream(httpResponse.body().byteStream());
    }
    else {
      dockerResponse.setStream(null);
    }
    return dockerResponse;
  }

  private void consumeResponseBody(EngineResponse response, Object content, EngineRequest config) throws IOException {
    if (content instanceof Source) {
      if (config.isAsync()) {
        response.setStream(Okio.buffer((Source) content).inputStream());
      }
      else if (config.getStdout() != null) {
        response.setStream(null);
        Okio.buffer(Okio.sink(config.getStdout())).writeAll((Source) content);
      }
      else if (response.getContentLength() != null && Integer.parseInt(response.getContentLength()) >= 0) {
        response.setStream(null);
        response.setContent(Okio.buffer((Source) content).readUtf8());
      }
      else {
        response.setStream(Okio.buffer((Source) content).inputStream());
      }
    }
    else if (content instanceof InputStream) {
      if (config.isAsync()) {
        response.setStream((InputStream) content);
      }
      else if (config.getStdout() != null) {
        IOUtils.copy((InputStream) content, config.getStdout());
        response.setStream(null);
      }
      else if (response.getContentLength() != null && Integer.parseInt(response.getContentLength()) >= 0) {
        response.setContent(IOUtils.toString((InputStream) content));
        response.setStream(null);
      }
      else {
        response.setStream((InputStream) content);
      }
    }
    else {
      response.setContent(content);
      response.setStream(null);
    }
  }

  /**
   * @see #ensureValidRequestConfig(EngineRequest)
   * @deprecated use ensureValidRequestConfig(EngineRequest)
   */
  @Deprecated
  private EngineRequest ensureValidRequestConfig(final Map<String, Object> config, RequestMethod method) {
    if (config == null || config.get("path") == null) {
      log.error("bad request config: " + config);
      throw new IllegalArgumentException("bad request config");
    }
    if (((String) config.get("path")).startsWith("/")) {
      config.put("path", ((String) config.get("path")).substring("/".length()));
    }
    config.put("method", method.name());

    EngineRequest engineRequest = new EngineRequest(method, (String) config.get("path"));
    engineRequest.setTimeout(config.get("timeout") == null ? 0 : (Integer) config.get("timeout"));
    engineRequest.setHeaders((Map<String, String>) config.get("headers"));
    Map<String, Object> query = (Map<String, Object>) config.get("query");
    engineRequest.setQuery(coerceValuesToListOfString(query));

    engineRequest.setContentType((String) config.get("requestContentType"));
    engineRequest.setBody(config.get("body"));

    engineRequest.setAsync(config.get("async") != null && (Boolean) config.get("async"));
    engineRequest.setStdout((OutputStream) config.get("stdout"));

    engineRequest.setApiVersion((String) config.get("apiVersion"));
    return engineRequest;
  }

  private EngineRequest ensureValidRequestConfig(final EngineRequest config) {
    if (config == null || config.getPath() == null) {
      log.error("bad request config: " + config);
      throw new IllegalArgumentException("bad request config");
    }
    if ((config.getPath()).startsWith("/")) {
      config.setPath(config.getPath().substring("/".length()));
    }
    return config;
  }

  private Map<String, List<String>> coerceValuesToListOfString(Map<String, Object> queryParameters) {
    if (queryParameters == null || queryParameters.isEmpty()) {
      return new HashMap<>();
    }
    return queryParameters.entrySet().stream()
        .collect(Collectors.toMap(Map.Entry::getKey, ((Map.Entry<String, Object> e) -> convert(e.getValue()))));
  }

  private List<String> convert(Object value) {
    if (value instanceof String[]) {
      return Arrays.stream((String[]) value).collect(Collectors.toList());
    }
    else if (value instanceof Collection) {
      return ((Collection<Object>) value).stream()
          .map(Object::toString)
          .collect(Collectors.toList());
    }
    else if (value != null) {
      return Collections.singletonList(value.toString());
    }
    else {
      return Collections.singletonList("");
    }
  }

  public String queryToString(Map<String, List<String>> queryParameters) {
    if (queryParameters == null || queryParameters.isEmpty()) {
      return "";
    }
    return queryParameters.entrySet().stream()
        .map((Map.Entry<String, List<String>> e) -> {
          String key = e.getKey();
          List<String> value = e.getValue();
          if (value != null) {
            return value.stream()
                .map((s) -> asUrlEncodedQuery(key, s))
                .collect(Collectors.joining("&"));
          }
          else {
            return asUrlEncodedQuery(key, "");
          }
        })
        .collect(Collectors.joining("&"));
  }

  private String asUrlEncodedQuery(String key, String value) {
    try {
      return URLEncoder.encode(key, "UTF-8") + "=" + URLEncoder.encode(value, "UTF-8");
    }
    catch (UnsupportedEncodingException e) {
      log.error("Url encoding failed for key=" + key + ",value=" + value, e);
      throw new RuntimeException("Url encoding failed", e);
    }
  }

  public String getMimeType(String contentTypeHeader) {
    if (contentTypeHeader == null) {
      return null;
    }
    return contentTypeHeader.replace(" ", "").split(";")[0];
  }

  public String getCharset(String contentTypeHeader) {
    String charset = "utf-8";
    Matcher matcher = Pattern.compile("[^;]+;\\s*charset=([^;]+)(;[^;]*)*").matcher(contentTypeHeader);
    if (matcher.find()) {
      charset = matcher.group(1);
    }
    return charset;
  }

  Map<String, Object> getSocketFactories() {
    return socketFactories;
  }

  DockerClientConfig getDockerClientConfig() {
    return dockerClientConfig;
  }

  void setProxy(Proxy proxy) {
    this.proxy = proxy;
  }
}
