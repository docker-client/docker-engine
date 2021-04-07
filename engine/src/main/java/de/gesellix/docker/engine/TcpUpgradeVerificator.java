package de.gesellix.docker.engine;

import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.ProtocolException;

public class TcpUpgradeVerificator {

  private static final Logger log = LoggerFactory.getLogger(TcpUpgradeVerificator.class);

  public static void ensureTcpUpgrade(final Response response) throws ProtocolException {
    if (response.code() != 101) {
      log.error("expected status 101, but got " + response.code() + " " + response.message());
      throw new ProtocolException("Expected HTTP 101 Connection Upgrade");
    }

    final String headerConnection = response.header("Connection");
    if (headerConnection == null || !headerConnection.equalsIgnoreCase("upgrade")) {
      log.error("expected 'Connection: Upgrade', but got 'Connection: " + headerConnection + "'");
      throw new ProtocolException("Expected 'Connection: Upgrade'");
    }

    final String headerUpgrade = response.header("Upgrade");
    if (headerUpgrade == null || !headerUpgrade.equalsIgnoreCase("tcp")) {
      log.error("expected 'Upgrade: tcp', but got 'Upgrade: " + headerUpgrade + "'");
      throw new ProtocolException("Expected 'Upgrade: tcp'");
    }
  }
}
