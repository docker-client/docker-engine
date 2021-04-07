package de.gesellix.docker.engine;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import de.gesellix.docker.client.filesocket.NamedPipeSocket;
import de.gesellix.docker.client.filesocket.NamedPipeSocketFactory;
import de.gesellix.docker.client.filesocket.UnixSocket;
import de.gesellix.docker.client.filesocket.UnixSocketFactory;
import de.gesellix.docker.client.filesocket.UnixSocketFactorySupport;
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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class OkDockerClient implements EngineClient {

  private static final Logger log = LoggerFactory.getLogger(OkDockerClient.class);

  private final Map<String, Object> socketFactories = new LinkedHashMap<>();

  private final DockerClientConfig dockerClientConfig;
  private Proxy proxy;

  private final JsonAdapter<Map> jsonMapAdapter;

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

    this.jsonMapAdapter = new Moshi.Builder().build().adapter(Map.class);
  }

  @Override
  public EngineResponse head(Map<String, Object> requestConfig) {
    Map<String, Object> config = ensureValidRequestConfig(requestConfig);
    config.put("method", "HEAD");
    return request(config);
  }

  @Override
  public EngineResponse get(Map<String, Object> requestConfig) {
    Map<String, Object> config = ensureValidRequestConfig(requestConfig);
    config.put("method", "GET");
    return request(config);
  }

  @Override
  public EngineResponse put(Map<String, Object> requestConfig) {
    Map<String, Object> config = ensureValidRequestConfig(requestConfig);
    config.put("method", "PUT");
    return request(config);
  }

  @Override
  public EngineResponse post(Map<String, Object> requestConfig) {
    Map<String, Object> config = ensureValidRequestConfig(requestConfig);
    config.put("method", "POST");
    return request(config);
  }

  @Override
  public EngineResponse delete(Map<String, Object> requestConfig) {
    Map<String, Object> config = ensureValidRequestConfig(requestConfig);
    config.put("method", "DELETE");
    return request(config);
  }

  @Override
  public WebSocket webSocket(Map<String, Object> requestConfig, WebSocketListener listener) {
    Map<String, Object> config = ensureValidRequestConfig(requestConfig);
    config.put("method", "GET");

    Request.Builder requestBuilder = prepareRequest(new Request.Builder(), config);
    Request request = requestBuilder.build();

    final int timeout = config.get("timeout") == null ? 0 : (Integer) config.get("timeout");
    OkHttpClient.Builder clientBuilder = prepareClient(new OkHttpClient.Builder(), timeout);
    OkHttpClient client = newClient(clientBuilder);

    return client.newWebSocket(request, listener);
  }

  public EngineResponse request(Map<String, Object> requestConfig) {
    Map<String, Object> config = ensureValidRequestConfig(requestConfig);

    // https://docs.docker.com/engine/reference/api/docker_remote_api_v1.24/#attach-to-a-container
    AttachConfig attachConfig = null;
    if (requestConfig.get("attach") != null) {
      Map<String, String> headers = (Map<String, String>) config.get("headers");
      if (headers == null) {
        headers = new HashMap<>();
      }
      config.put("headers", headers);
      headers.put("Upgrade", "tcp");
      headers.put("Connection", "Upgrade");
      attachConfig = (AttachConfig) requestConfig.get("attach");
    }
//        boolean multiplexStreams = config.multiplexStreams

    Request.Builder requestBuilder = prepareRequest(new Request.Builder(), config);
    final Request request = requestBuilder.build();

    final int timeout = config.get("timeout") == null ? 0 : (Integer) config.get("timeout");
    OkHttpClient.Builder clientBuilder = prepareClient(new OkHttpClient.Builder(), timeout);
    OkResponseCallback responseCallback = null;
    if (attachConfig != null) {
      ConnectionProvider connectionProvider = new ConnectionProvider();
      clientBuilder.addNetworkInterceptor(connectionProvider);
      responseCallback = new OkResponseCallback(connectionProvider, attachConfig);
    }
    final OkHttpClient client = newClient(clientBuilder);

    log.debug(request.method() + " " + request.url() + " using proxy: " + client.proxy());

    Call call = client.newCall(request);
    if (responseCallback != null) {
      call.enqueue(responseCallback);
      log.debug("request enqueued");
      EngineResponse dockerResponse = new EngineResponse();
      dockerResponse.setResponseCallback(responseCallback);
      return dockerResponse;
    }
    else {
      EngineResponse dockerResponse;
      try {
        Response response = call.execute();
        log.debug("response: " + response);
        dockerResponse = handleResponse(response, config);
        if (dockerResponse.getStream() == null) {
//            log.warn("closing response...")
          response.close();
        }
      }
      catch (Exception e) {
        log.error("Request failed", e);
        throw new RuntimeException("Request failed", e);
      }
      return dockerResponse;
    }
  }

  private Request.Builder prepareRequest(final Request.Builder builder, final Map<String, Object> config) {
    String method = (String) config.get("method");
    String contentType = (String) config.get("requestContentType");
    Map<String, String> additionalHeaders = (Map<String, String>) config.get("headers");
    Object body = config.get("body");

    String protocol = dockerClientConfig.getScheme();
    String host = dockerClientConfig.getHost();
    int port = dockerClientConfig.getPort();

    String path = (String) config.get("path");
    if (config.get("apiVersion") != null) {
      path = config.get("apiVersion") + "/" + path;
    }
    String queryAsString;
    if (config.get("query") != null) {
      queryAsString = queryToString((Map) config.get("query"));
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
        UnixSocketFactory unixSocketFactory = (UnixSocketFactory) socketFactories.get(protocol);
        builder
            .socketFactory(unixSocketFactory)
            .dns(unixSocketFactory)
            .build();
        break;
      case "npipe":
        NamedPipeSocketFactory npipeSocketFactory = (NamedPipeSocketFactory) socketFactories.get(protocol);
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
        httpUrl = urlBuilder
            .scheme("http")
            .host(new UnixSocket().encodeHostname(host))
//                    .port(/not/allowed/for/unix/socket/)
            .build();
        break;
      case "npipe":
        httpUrl = urlBuilder
            .scheme("http")
            .host(new NamedPipeSocket().encodeHostname(host))
//                    .port(/not/allowed/for/npipe/socket/)
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
          requestBody = RequestBody.create(jsonMapAdapter.toJson((Map) body), MediaType.parse(contentType));
          break;
        case "application/octet-stream":
        default:
          Source source = Okio.source((InputStream) body);
          BufferedSource buffer = Okio.buffer(source);
          try {
            requestBody = RequestBody.create(buffer.readByteArray(), MediaType.parse(contentType));
          }
          catch (IOException e) {
            log.error("Failed to read request body", e);
            throw new RuntimeException("Failed to read request body", e);
          }
          break;
      }
    }
    return requestBody;
  }

  public EngineResponse handleResponse(Response httpResponse, Map config) throws IOException {
    final EngineResponse response = readHeaders(httpResponse);

    if (response.getStatus().getCode() == 204) {
      if (response.getStream() != null) {
        // redirect the response body to /dev/null, since it's expected to be empty
        IOUtils.consumeToDevNull(response.getStream());
      }
      return response;
    }

    String mimeType = response.getMimeType();
    if (mimeType == null) {
      mimeType = "";
    }
    switch (mimeType) {
      case "application/vnd.docker.raw-stream":
        InputStream rawStream = new RawInputStream(httpResponse.body().byteStream());
        if (config.get("stdout") != null) {
          log.debug("redirecting to stdout.");
          IOUtils.copy(rawStream, (OutputStream) config.get("stdout"));
          response.setStream(null);
        }
        else {
          response.setStream(rawStream);
        }
        break;
      case "application/json":
        if (config.get("async") != null && (Boolean) config.get("async")) {
          consumeResponseBody(response, httpResponse.body().source(), config);
        }
        else {
          Object content = new JsonContentHandler().getContent(httpResponse.body().source());
          consumeResponseBody(response, content, config);
        }
        break;
      case "text/html":
      case "text/plain":
        InputStream text = httpResponse.body().byteStream();
        consumeResponseBody(response, text, config);
        break;
      case "application/octet-stream":
        InputStream octet = httpResponse.body().byteStream();
        log.debug("passing through via `response.stream`.");
        if (config.get("stdout") != null) {
          IOUtils.copy(octet, (OutputStream) config.get("stdout"));
          response.setStream(null);
        }
        else {
          response.setStream(octet);
        }
        break;
      case "application/x-tar":
        if (response.getStream() != null) {
          if (config.get("stdout") != null) {
            log.debug("redirecting to stdout.");
            IOUtils.copy(response.getStream(), (OutputStream) config.get("stdout"));
            response.setStream(null);
          }
          else {
            log.info(response.getMimeType() + " stream won't be consumed, but is available in the response.");
          }
        }
        break;
      default:
        log.debug("unexpected mime type '" + response.getMimeType() + "'.");
        ResponseBody body = httpResponse.body();
        if (body.contentLength() == -1) {
          InputStream stream = body.byteStream();
          log.debug("passing through via `response.stream`.");
          if (config.get("stdout") != null) {
            IOUtils.copy(stream, (OutputStream) config.get("stdout"));
            response.setStream(null);
          }
          else {
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
    log.debug("status: " + dockerResponse.getStatus());

    final Headers headers = httpResponse.headers();
    log.debug("headers: \n" + headers);
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

  private void consumeResponseBody(EngineResponse response, Object content, Map config) throws IOException {
    if (content instanceof Source) {
      if (config.get("async") != null && ((Boolean) config.get("async"))) {
        response.setStream(Okio.buffer((Source) content).inputStream());
      }
      else if (config.get("stdout") != null) {
        response.setStream(null);
        Okio.buffer(Okio.sink((OutputStream) config.get("stdout"))).writeAll((Source) content);
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
      if (config.get("async") != null && ((Boolean) config.get("async"))) {
        response.setStream((InputStream) content);
      }
      else if (config.get("stdout") != null) {
        IOUtils.copy((InputStream) content, (OutputStream) config.get("stdout"));
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

  private Map<String, Object> ensureValidRequestConfig(final Map<String, Object> config) {
    if (config == null || config.get("path") == null) {
      log.error("bad request config: " + config);
      throw new IllegalArgumentException("bad request config");
    }
    if (((String) config.get("path")).startsWith("/")) {
      config.put("path", ((String) config.get("path")).substring("/".length()));
    }
    return config;
  }

  public String queryToString(Map<String, Object> queryParameters) {
    if (queryParameters == null || queryParameters.isEmpty()) {
      return "";
    }
    return queryParameters.entrySet().stream()
        .map((Map.Entry<String, Object> e) -> {
          String key = e.getKey();
          Object value = e.getValue();
          if (value instanceof String[]) {
            return Arrays.stream((String[]) value)
                .map((s) -> asUrlEncodedQuery(key, s))
                .collect(Collectors.joining("&"));
          }
          else if (value instanceof Collection) {
            return ((Collection<String>) value).stream()
                .map((s) -> asUrlEncodedQuery(key, s))
                .collect(Collectors.joining("&"));
          }
          else if (value != null) {
            return asUrlEncodedQuery(key, value.toString());
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
