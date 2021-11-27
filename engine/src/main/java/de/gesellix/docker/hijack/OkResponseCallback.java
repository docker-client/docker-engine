package de.gesellix.docker.hijack;

import de.gesellix.docker.engine.AttachConfig;
import de.gesellix.docker.engine.TcpUpgradeVerificator;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class OkResponseCallback implements Callback {

  private static final Logger log = LoggerFactory.getLogger(OkResponseCallback.class);

  private final AttachConfig attachConfig;

  public OkResponseCallback(AttachConfig attachConfig) {
    this.attachConfig = attachConfig;
  }

  @Override
  public void onFailure(Call call, final IOException e) {
    log.error("connection failed: " + e.getMessage(), e);
    attachConfig.onFailure(e);
  }

  @Override
  public void onResponse(final Call call, final Response response) throws IOException {
    TcpUpgradeVerificator.ensureTcpUpgrade(response);
    log.debug("Response content type: " + response.header("Content-Type"));
    attachConfig.onResponse(response);
  }
}
