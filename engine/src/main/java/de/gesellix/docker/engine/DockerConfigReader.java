package de.gesellix.docker.engine;

import com.squareup.moshi.Moshi;
import okio.Okio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.Map;

public class DockerConfigReader {

  private static final Logger log = LoggerFactory.getLogger(DockerConfigReader.class);

  public File configFile = new File(System.getProperty("user.home") + "/.docker", "config.json");

  public File legacyConfigFile = new File(System.getProperty("user.home"), ".dockercfg");

  private File dockerConfigFile = null;

  private final Moshi moshi = new Moshi.Builder().build();

  /**
   * Visible internally and for tests
   *
   * @deprecated should ony be used in tests
   */
  @Deprecated
  public void resetDockerConfigFile() {
    setDockerConfigFile(null);
  }

  public void setDockerConfigFile(File dockerConfigFile) {
    this.dockerConfigFile = dockerConfigFile;
  }

  public File getDockerConfigFile() {
    if (dockerConfigFile == null) {
      dockerConfigFile = resolveDockerConfigFile(configFile, legacyConfigFile);
    }
    return dockerConfigFile;
  }

  public File resolveDockerConfigFile(File defaultConfigFile, File legacyConfigFile) {
    String dockerConfig = System.getProperty("docker.config", System.getenv("DOCKER_CONFIG"));
    if (dockerConfig != null && !dockerConfig.isEmpty()) {
      return new File(dockerConfig, "config.json");
    } else if (defaultConfigFile.exists()) {
      return defaultConfigFile;
    } else if (legacyConfigFile.exists()) {
      return legacyConfigFile;
    }
    log.warn("docker config file not found, assuming '{}' as fallback", defaultConfigFile);
    return defaultConfigFile;
  }

  public Map readDockerConfigFile() {
    return readDockerConfigFile(null);
  }

  public Map readDockerConfigFile(File dockerCfg) {
    if (dockerCfg == null) {
      dockerCfg = getDockerConfigFile();
    }
    if (dockerCfg == null || !dockerCfg.exists()) {
      log.info("docker config '{}' doesn't exist", dockerCfg);
      return Collections.emptyMap();
    }
    log.debug("reading config from {}", dockerCfg);
    try {
      return moshi.adapter(Map.class).fromJson(Okio.buffer(Okio.source(dockerCfg)));
    } catch (Exception e) {
      log.debug(MessageFormat.format("failed to read config from {}", dockerCfg), e);
      return Collections.emptyMap();
    }
  }
}
