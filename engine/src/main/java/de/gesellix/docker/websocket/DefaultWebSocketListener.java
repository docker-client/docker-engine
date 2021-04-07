package de.gesellix.docker.websocket;

import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultWebSocketListener extends WebSocketListener {

  private final static Logger log = LoggerFactory.getLogger(DefaultWebSocketListener.class);

  @Override
  public void onOpen(@NotNull WebSocket webSocket, @NotNull Response response) {
    log.debug("[onOpen]");
  }

  @Override
  public void onFailure(@NotNull WebSocket webSocket, final Throwable t, Response response) {
    log.debug("[onFailure] {}", t.getMessage());
    t.printStackTrace();
  }

  @Override
  public void onMessage(@NotNull WebSocket webSocket, @NotNull final String text) {
    log.debug("[onMessage.text] {}", text);
  }

  @Override
  public void onMessage(@NotNull WebSocket webSocket, final ByteString bytes) {
    log.debug("[onMessage.binary] size: {}", bytes.size());
  }

  @Override
  public void onClosing(@NotNull WebSocket webSocket, final int code, @NotNull final String reason) {
    log.debug("[onClosing] {}/{}", code, reason);
  }

  @Override
  public void onClosed(@NotNull WebSocket webSocket, final int code, @NotNull final String reason) {
    log.debug("[onClosed] {}/{}", code, reason);
  }
}
