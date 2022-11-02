package de.gesellix.docker.engine;

import de.gesellix.docker.context.ContextStore;
import de.gesellix.docker.context.DockerContextResolver;
import de.gesellix.docker.context.EndpointMetaBase;

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

  private DockerConfigReader dockerConfigReader;
  private DockerContextResolver dockerContextResolver;

  private String contextsDirectoryName = "contexts";

  private File dockerContextStoreDir = null;

  public static final String dockerEndpointDefaultName = "docker";
  public static final String dockerDefaultContextName = "default";

  private String apiVersion = System.getProperty("docker.api.version", System.getenv("DOCKER_API_VERSION"));

  private String tmpdir = System.getProperty("docker.tmpdir", System.getenv("DOCKER_TMPDIR"));

  private String dockerContentTrust = System.getProperty("docker.content.trust", System.getenv("DOCKER_CONTENT_TRUST"));

  private String contentTrustServer = System.getProperty("docker.content.trust.server", System.getenv("DOCKER_CONTENT_TRUST_SERVER"));

  private String officialNotaryServer = "https://notary.docker.io";

  public DockerEnv() {
    this(null);
  }

  public DockerEnv(String dockerHost) {
    // TODO allow configuration via "config file provider" for lazy config file resolution
    this.dockerConfigReader = new DockerConfigReader();
    this.dockerContextResolver = new DockerContextResolver();
    this.resetDockerHostFromCurrentConfig(dockerHost);
  }

  /**
   * Visible internally and for tests
   *
   * @deprecated should ony be used in tests
   */
  @Deprecated
  void resetDockerHostFromCurrentConfig() {
    this.resetDockerHostFromCurrentConfig(null);
  }

  /**
   * Visible internally and for tests
   *
   * @param dockerHostOverride optional override of any other configuration inputs
   * @deprecated should ony be used in tests
   */
  @Deprecated
  void resetDockerHostFromCurrentConfig(String dockerHostOverride) {
    this.dockerContextStoreDir = null;
    getDockerConfigReader().resetDockerConfigFile();
    if (dockerHostOverride == null) {
      this.dockerHost = getDockerHostFromContextOrHostOrDefault();
    } else {
      this.dockerHost = dockerHostOverride;
    }
  }

  private String getDockerHostFromContextOrHostOrDefault() {
    // TODO allow configuration via "contexts directory provider" for lazy contexts directory resolution
    ContextStore store = new ContextStore(getDockerContextStoreDir());
    String dockerContextName = dockerContextResolver.resolveDockerContextName(getDockerConfigReader());
    EndpointMetaBase dockerEndpoint = dockerContextResolver.resolveDockerEndpoint(store, dockerContextName);
    if (dockerEndpoint != null && dockerEndpoint.getHost() != null) {
      return dockerEndpoint.getHost();
    } else {
      return getDefaultDockerHost();
    }
  }

  public static String getDockerHostFromSystemPropertyOrEnvironment() {
    String configuredDockerHost = System.getProperty("docker.host", System.getenv("DOCKER_HOST"));
    if (configuredDockerHost != null && !configuredDockerHost.isEmpty()) {
      return configuredDockerHost;
    }
    return null;
  }

  public static String getDockerContextFromSystemPropertyOrEnvironment() {
    String configuredDockerContext = System.getProperty("docker.context", System.getenv("DOCKER_CONTEXT"));
    if (configuredDockerContext != null && !configuredDockerContext.isEmpty()) {
      return configuredDockerContext;
    }
    return null;
  }

  public static String getDefaultDockerHost() {
    if (((String) System.getProperties().get("os.name")).toLowerCase().contains("windows")) {
      return "npipe:////./pipe/docker_engine";
    } else {
      return "unix:///var/run/docker.sock";
    }
  }

  public File getDockerConfigFile() {
    return dockerConfigReader.getDockerConfigFile();
  }

  public DockerConfigReader getDockerConfigReader() {
    return dockerConfigReader;
  }

  public File getDockerContextStoreDir() {
    if (dockerContextStoreDir == null) {
      dockerContextStoreDir = new File(getDockerConfigFile().getParentFile(), contextsDirectoryName);
    }
    return dockerContextStoreDir;
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
