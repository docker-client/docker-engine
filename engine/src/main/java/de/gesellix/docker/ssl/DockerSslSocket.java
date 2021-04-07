package de.gesellix.docker.ssl;

import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;

public class DockerSslSocket {

  public SSLSocketFactory getSslSocketFactory() {
    return sslSocketFactory;
  }

  public void setSslSocketFactory(SSLSocketFactory sslSocketFactory) {
    this.sslSocketFactory = sslSocketFactory;
  }

  public X509TrustManager getTrustManager() {
    return trustManager;
  }

  public void setTrustManager(X509TrustManager trustManager) {
    this.trustManager = trustManager;
  }

  private SSLSocketFactory sslSocketFactory;
  private X509TrustManager trustManager;
}
