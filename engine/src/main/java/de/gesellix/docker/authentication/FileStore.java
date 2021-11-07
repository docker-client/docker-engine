package de.gesellix.docker.authentication;

import java.util.Base64;
import java.util.Map;
import java.util.stream.Collectors;

public class FileStore implements CredsStore {

  private final Map<String, Map> config;
  private transient Map<String, AuthConfig> allAuthConfigs;

  public FileStore(Map<String, Map> config) {
    this.config = config.containsKey("auths") ? (Map) config.get("auths") : config;
  }

  @Override
  public AuthConfig getAuthConfig(String registry) {
    final AuthConfig authConfig = getAuthConfigs().get(registry);
    return authConfig != null ? authConfig : AuthConfig.EMPTY_AUTH_CONFIG;
  }

  @Override
  public Map<String, AuthConfig> getAuthConfigs() {
    if (allAuthConfigs == null) {
      allAuthConfigs = config.entrySet().stream()
          .filter((e) -> e.getValue() != null && (e.getValue().get("auth") != null || e.getValue().containsKey("identitytoken")))
          .collect(Collectors.toMap(
              Map.Entry::getKey,
              e -> {
                String registry = e.getKey();
                Map value = e.getValue();

                AuthConfig authConfig = new AuthConfig();
                authConfig.setServeraddress(registry);

                if (value.containsKey("identitytoken")) {
                  authConfig.setIdentitytoken((String) value.get("identitytoken"));
                }
                else {
                  String[] login = new String(Base64.getDecoder().decode((String) value.get("auth"))).split(":");
                  String username = login[0];
                  String password = login[1];
                  authConfig.setUsername(username);
                  authConfig.setPassword(password);
                  authConfig.setEmail((String) value.get("email"));
                }
                return authConfig;
              }
          ));
    }
    return allAuthConfigs;
  }
}
