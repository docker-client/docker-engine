package de.gesellix.docker.context;

import java.util.Objects;

public class EndpointMetaBase {
  private String host;
  private Boolean skipTLSVerify;

  public EndpointMetaBase(String host, Boolean skipTLSVerify) {
    this.host = host;
    this.skipTLSVerify = skipTLSVerify;
  }

  public String getHost() {
    return host;
  }

  public Boolean getSkipTLSVerify() {
    return skipTLSVerify;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    EndpointMetaBase that = (EndpointMetaBase) o;
    return Objects.equals(getHost(), that.getHost()) && Objects.equals(getSkipTLSVerify(), that.getSkipTLSVerify());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getHost(), getSkipTLSVerify());
  }
}
