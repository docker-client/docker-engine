package de.gesellix.docker.authentication;

import de.gesellix.docker.engine.DockerConfigReader;
import de.gesellix.docker.engine.DockerEnv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Map;

import static de.gesellix.docker.authentication.AuthConfig.EMPTY_AUTH_CONFIG;

public class AuthConfigReader {

  private final static Logger log = LoggerFactory.getLogger(AuthConfigReader.class);

  private final DockerEnv env;
  private DockerConfigReader dockerConfigReader;

  public AuthConfigReader() {
    this(new DockerEnv());
  }

  public AuthConfigReader(DockerEnv env) {
    this.env = env;
    this.dockerConfigReader = env.getDockerConfigReader();
  }

  //  @Override
  public AuthConfig readDefaultAuthConfig() {
    return readAuthConfig(null, dockerConfigReader.getDockerConfigFile());
  }

  //  @Override
  public AuthConfig readAuthConfig(String hostname, File dockerCfg) {
    log.debug("read authConfig");

    if (hostname == null || hostname.trim().isEmpty()) {
      hostname = env.getIndexUrl_v1();
    }

    Map parsedDockerCfg = dockerConfigReader.readDockerConfigFile(dockerCfg);
    if (parsedDockerCfg == null || parsedDockerCfg.isEmpty()) {
      return EMPTY_AUTH_CONFIG;
    }

    CredsStore credsStore = getCredentialsStore(parsedDockerCfg, hostname);
    return credsStore.getAuthConfig(hostname);
  }

  public CredsStore getCredentialsStore(Map parsedDockerCfg) {
    return getCredentialsStore(parsedDockerCfg, "");
  }

  public CredsStore getCredentialsStore(Map parsedDockerCfg, String hostname) {
    if (parsedDockerCfg.containsKey("credHelpers") && hostname != null && !hostname.trim().isEmpty()) {
      return new NativeStore((String) ((Map) parsedDockerCfg.get("credHelpers")).get(hostname));
    }
    if (parsedDockerCfg.containsKey("credsStore")) {
      return new NativeStore((String) parsedDockerCfg.get("credsStore"));
    }
    return new FileStore(parsedDockerCfg);
  }
}
