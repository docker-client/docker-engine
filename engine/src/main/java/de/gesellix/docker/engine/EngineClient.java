package de.gesellix.docker.engine;

import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

import java.util.Map;

/**
 * Will be replaced with the implementation from <a href="https://github.com/docker-client/docker-remote-api-client">github.com/docker-client/docker-remote-api-client</a>.
 *
 * @deprecated
 */
@Deprecated
public interface EngineClient {

  EngineResponse request(EngineRequest requestConfig);

  EngineResponse head(Map<String, Object> requestConfig);

  EngineResponse get(Map<String, Object> requestConfig);

  EngineResponse put(Map<String, Object> requestConfig);

  EngineResponse post(Map<String, Object> requestConfig);

  EngineResponse delete(Map<String, Object> requestConfig);

  WebSocket webSocket(Map<String, Object> requestConfig, WebSocketListener listener);
}
