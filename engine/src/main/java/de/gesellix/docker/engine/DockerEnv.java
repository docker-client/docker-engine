package de.gesellix.docker.engine;

import java.io.File;

/**
 * Configuration via environment variables should work like
 * described in the official <a href="https://docs.docker.com/engine/reference/commandline/cli/#environment-variables">cli docs</a>.
 */
public class DockerEnv {

  private String dockerHost;

  private int defaultTlsPort = 2376;

  private String tlsVerify = System.getProperty("docker.tls.verify", System.getenv("DOCKER_TLS_VERIFY"));

  private String certPath = System.getProperty("docker.cert.path", System.getenv("DOCKER_CERT_PATH"));

  private String defaultCertPath = new File((String) System.getProperties().get("user.home"), ".docker").getAbsolutePath();

  // the v1 registry still seems to be valid for authentication.
  private final String indexUrl_v1 = "https://index.docker.io/v1/";
  private final String indexUrl_v2 = "https://registry-1.docker.io";

  private File configFile = new File(System.getProperty("user.home") + "/.docker", "config.json");

  private File legacyConfigFile = new File(System.getProperty("user.home"), ".dockercfg");

  private File dockerConfigFile = null;

  private String apiVersion = System.getProperty("docker.api.version", System.getenv("DOCKER_API_VERSION"));

  private String tmpdir = System.getProperty("docker.tmpdir", System.getenv("DOCKER_TMPDIR"));

  private String dockerContentTrust = System.getProperty("docker.content.trust", System.getenv("DOCKER_CONTENT_TRUST"));

  private String contentTrustServer = System.getProperty("docker.content.trust.server", System.getenv("DOCKER_CONTENT_TRUST_SERVER"));

  private String officialNotaryServer = "https://notary.docker.io";

  public DockerEnv() {
    this(getDockerHostOrDefault());
  }

  public DockerEnv(String dockerHost) {
    this.dockerHost = dockerHost;
  }

  public static String getDockerHostOrDefault() {
    String configuredDockerHost = System.getProperty("docker.host", System.getenv("DOCKER_HOST"));
    if (configuredDockerHost != null && !configuredDockerHost.isEmpty()) {
      return configuredDockerHost;
    }
    else {
      if (((String) System.getProperties().get("os.name")).toLowerCase().contains("windows")) {
        // default to non-tls http
        //return "tcp://localhost:2375"

        // or use a named pipe:
        return "npipe:////./pipe/docker_engine";
      }
      else {
        return "unix:///var/run/docker.sock";
      }
    }
  }

  public void setDockerConfigFile(File dockerConfigFile) {
    this.dockerConfigFile = dockerConfigFile;
  }

  public File getDockerConfigFile() {
    if (dockerConfigFile == null) {
      String dockerConfig = System.getProperty("docker.config", System.getenv("DOCKER_CONFIG"));
      if (dockerConfig != null && !dockerConfig.isEmpty()) {
        this.dockerConfigFile = new File(dockerConfig, "config.json");
      }
      else if (configFile.exists()) {
        this.dockerConfigFile = configFile;
      }
      else if (legacyConfigFile.exists()) {
        this.dockerConfigFile = legacyConfigFile;
      }
    }

    return dockerConfigFile;
  }

  public String getDockerHost() {
    return dockerHost;
  }

  public void setDockerHost(String dockerHost) {
    this.dockerHost = dockerHost;
  }

  public int getDefaultTlsPort() {
    return defaultTlsPort;
  }

  public void setDefaultTlsPort(int defaultTlsPort) {
    this.defaultTlsPort = defaultTlsPort;
  }

  public String getTlsVerify() {
    return tlsVerify;
  }

  public void setTlsVerify(String tlsVerify) {
    this.tlsVerify = tlsVerify;
  }

  public String getCertPath() {
    return certPath;
  }

  public void setCertPath(String certPath) {
    this.certPath = certPath;
  }

  public String getDefaultCertPath() {
    return defaultCertPath;
  }

  public void setDefaultCertPath(String defaultCertPath) {
    this.defaultCertPath = defaultCertPath;
  }

  public String getIndexUrl_v1() {
    return indexUrl_v1;
  }

  public String getIndexUrl_v2() {
    return indexUrl_v2;
  }

  public File getConfigFile() {
    return configFile;
  }

  public void setConfigFile(File configFile) {
    this.configFile = configFile;
  }

  public File getLegacyConfigFile() {
    return legacyConfigFile;
  }

  public void setLegacyConfigFile(File legacyConfigFile) {
    this.legacyConfigFile = legacyConfigFile;
  }

  public String getApiVersion() {
    return apiVersion;
  }

  public void setApiVersion(String apiVersion) {
    this.apiVersion = apiVersion;
  }

  public String getTmpdir() {
    return tmpdir;
  }

  public void setTmpdir(String tmpdir) {
    this.tmpdir = tmpdir;
  }

  public String getDockerContentTrust() {
    return dockerContentTrust;
  }

  public void setDockerContentTrust(String dockerContentTrust) {
    this.dockerContentTrust = dockerContentTrust;
  }

  public String getContentTrustServer() {
    return contentTrustServer;
  }

  public void setContentTrustServer(String contentTrustServer) {
    this.contentTrustServer = contentTrustServer;
  }

  public String getOfficialNotaryServer() {
    return officialNotaryServer;
  }

  public void setOfficialNotaryServer(String officialNotaryServer) {
    this.officialNotaryServer = officialNotaryServer;
  }
}
