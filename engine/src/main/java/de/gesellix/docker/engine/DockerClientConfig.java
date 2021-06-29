package de.gesellix.docker.engine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class DockerClientConfig {

  private final static Logger log = LoggerFactory.getLogger(DockerClientConfig.class);

  private DockerEnv env;
  private String scheme;
  private String host;
  private int port;
  private String certPath;

  public DockerClientConfig() {
    this(new DockerEnv());
  }

  public DockerClientConfig(String dockerHost) {
    this(new DockerEnv(dockerHost));
  }

  public DockerClientConfig(DockerEnv config) {
    apply(config);
  }

  public void apply(DockerEnv env) {
    if (env.getDockerHost() == null || env.getDockerHost().isEmpty()) {
      throw new IllegalStateException("dockerHost must be set");
    }
    this.env = env;

    Map<String, String> dockerClientConfig;
    try {
      dockerClientConfig = getActualConfig(env);
    }
    catch (MalformedURLException e) {
      log.error("Invalid DOCKER_HOST " + env.getDockerHost(), e);
      throw new RuntimeException("Invalid DOCKER_HOST " + env.getDockerHost(), e);
    }
    this.scheme = dockerClientConfig.get("protocol");
    this.host = dockerClientConfig.get("host");
    this.port = Integer.parseInt(dockerClientConfig.get("port"));
    this.certPath = dockerClientConfig.get("certPath");
  }

  Map<String, String> getActualConfig(DockerEnv env) throws MalformedURLException {
    String dockerHost = env.getDockerHost();
    if (dockerHost == null || dockerHost.isEmpty()) {
      throw new IllegalStateException("dockerHost must be set");
    }
    final String oldProtocol = dockerHost.split("://", 2)[0];
    String protocol = oldProtocol;
    final Map<String, String> result = new HashMap<>();
    switch (protocol) {
      case "http":
      case "https":
      case "tcp":
        URL candidateURL = new URL(dockerHost.replaceFirst("^" + oldProtocol + "://", "https://"));
        TlsConfig tlsConfig = getTlsConfig(candidateURL, env);
        if (tlsConfig.getTlsVerify()) {
          protocol = "https";
          result.put("certPath", tlsConfig.getCertPath());
        }
        else {
          protocol = "http";
          result.put("certPath", null);
        }

        URL tcpUrl = new URL(dockerHost.replaceFirst("^" + oldProtocol + "://", protocol + "://"));
        result.put("protocol", tcpUrl.getProtocol());
        result.put("host", tcpUrl.getHost());
        result.put("port", String.valueOf(tcpUrl.getPort()));
        break;
      case "unix":
        String dockerUnixSocket = dockerHost.replaceFirst("unix://", "");
        result.put("protocol", "unix");
        result.put("host", dockerUnixSocket);
        result.put("port", String.valueOf(-1));
        result.put("certPath", null);
        break;
      case "npipe":
        String dockerNamedPipe = dockerHost.replaceFirst("npipe://", "");
        result.put("protocol", "npipe");
        result.put("host", dockerNamedPipe);
        result.put("port", String.valueOf(-1));
        result.put("certPath", null);
        break;
      default:
        log.warn("protocol '" + protocol + "' not supported");
        URL url = new URL(dockerHost);
        result.put("protocol", url.getProtocol());
        result.put("host", url.getHost());
        result.put("port", String.valueOf(url.getPort()));
        result.put("certPath", null);
        break;
    }
    log.debug("selected dockerHost at '" + result + "'");
    return result;
  }

  public TlsConfig getTlsConfig(URL candidateURL, final DockerEnv env) {
    // Setting env.DOCKER_TLS_VERIFY to the empty string disables tls verification,
    // while any other value (including "0" or "false") enables tls verification.
    // See https://docs.docker.com/engine/reference/commandline/cli/#environment-variables
    // for the official docs and https://github.com/moby/moby/issues/22411 for a detailed
    // discussion about enabling/disabling TLS verification in Docker.

    // explicitly disabled?
    if (env.getTlsVerify() != null && env.getTlsVerify().equals("")) {
      log.debug("dockerTlsVerify='" + env.getTlsVerify() + "'");
      return new TlsConfig(false, null);
    }

    String certPath = getCertPathOrNull(env);
    final Boolean certsPathExists = certPath != null;

    // explicitly enabled?
    if (env.getTlsVerify() != null && !env.getTlsVerify().isEmpty()) {
      if (!certsPathExists) {
        throw new IllegalStateException("tlsverify='" + env.getTlsVerify() + "', but '" + env.getCertPath() + "' doesn't exist");
      }
      else {
        log.debug("certsPathExists=" + certsPathExists);
        return new TlsConfig(true, certPath);
      }
    }

    // make a guess if we could use tls, when it's neither explicitly enabled nor disabled
    final Boolean isTlsPort = candidateURL.getPort() == env.getDefaultTlsPort();
    log.debug("certsPathExists=" + certsPathExists + ", isTlsPort=" + isTlsPort);
    return new TlsConfig(certsPathExists && isTlsPort, certPath);
  }

  public String getCertPathOrNull(final DockerEnv env) {
    Boolean certsPathExists = env.getCertPath() != null && !env.getCertPath().isEmpty() && new File(env.getCertPath(), "").isDirectory();
    if (!certsPathExists) {
      if (env.getDefaultCertPath() != null && !env.getDefaultCertPath().isEmpty() && new File(env.getDefaultCertPath(), "").isDirectory()) {
        log.debug("defaultDockerCertPath=" + env.getDefaultCertPath());
        return env.getDefaultCertPath();
      }
      return null;
    }
    else {
      log.debug("dockerCertPath=" + env.getCertPath());
      return env.getCertPath();
    }
  }

  public Boolean isContentTrustEnabled(DockerEnv env) {
    if (env.getDockerContentTrust().trim().equals("") || isFalsy(env.getDockerContentTrust())) {
      return false;
    }
    // is a truthy value or any other (non-empty and non-falsy) value
    return true;
  }

  public static Boolean isFalsy(String value) {
    String sanitizedValue = value.trim().toLowerCase();
    return Arrays.asList("0", "false", "no").contains(sanitizedValue);
  }

  public DockerEnv getEnv() {
    return env;
  }

  public void setEnv(DockerEnv env) {
    this.env = env;
  }

  public String getScheme() {
    return scheme;
  }

  public String getHost() {
    return host;
  }

  public int getPort() {
    return port;
  }

  public String getCertPath() {
    return certPath;
  }

  public static class TlsConfig {

    private boolean tlsVerify = false;
    private String certPath = null;

    public TlsConfig(boolean tlsVerify, String certPath) {
      this.tlsVerify = tlsVerify;
      this.certPath = certPath;
    }

    public boolean getTlsVerify() {
      return tlsVerify;
    }

    public boolean isTlsVerify() {
      return tlsVerify;
    }

    public void setTlsVerify(boolean tlsVerify) {
      this.tlsVerify = tlsVerify;
    }

    public String getCertPath() {
      return certPath;
    }

    public void setCertPath(String certPath) {
      this.certPath = certPath;
    }
  }
}
