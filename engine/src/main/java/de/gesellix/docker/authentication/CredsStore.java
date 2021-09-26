package de.gesellix.docker.authentication;

import java.util.Map;

public interface CredsStore {

  String TOKEN_USERNAME = "<token>";

  AuthConfig getAuthConfig(String registry);

  Map<String, AuthConfig> getAuthConfigs();
}
