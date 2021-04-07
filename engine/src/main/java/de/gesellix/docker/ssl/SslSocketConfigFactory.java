package de.gesellix.docker.ssl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;

public class SslSocketConfigFactory {

  private static final Logger log = LoggerFactory.getLogger(SslSocketConfigFactory.class);

  public DockerSslSocket createDockerSslSocket(String certPath) {
    SSLContext sslContext;
    X509TrustManager trustManager;
    try {
      KeyStore keyStore = createKeyStore(certPath);
      KeyManagerFactory keyManagerFactory = initKeyManagerFactory(keyStore);
      TrustManagerFactory tmf = initTrustManagerFactory(keyStore);
      trustManager = getUniqueX509TrustManager(tmf);
      sslContext = initSslContext(keyManagerFactory, trustManager);
    }
    catch (Exception e) {
      log.error("SSL initialization failed", e);
      throw new RuntimeException("SSL initialization failed", e);
    }

    DockerSslSocket socket = new DockerSslSocket();
    socket.setSslSocketFactory(sslContext.getSocketFactory());
    socket.setTrustManager(trustManager);
    return socket;
  }

  private SSLContext initSslContext(KeyManagerFactory keyManagerFactory, X509TrustManager trustManager) throws NoSuchAlgorithmException, KeyManagementException {
    SSLContext sslContext = SSLContext.getInstance("TLS");
    sslContext.init(keyManagerFactory.getKeyManagers(), new TrustManager[] {trustManager}, null);
    return sslContext;
  }

  private X509TrustManager getUniqueX509TrustManager(TrustManagerFactory trustManagerFactory) {
    final TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
    if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
      throw new IllegalStateException("Unexpected default trust managers");
    }

    return (X509TrustManager) trustManagers[0];
  }

  private TrustManagerFactory initTrustManagerFactory(KeyStore keyStore) throws NoSuchAlgorithmException, KeyStoreException {
    TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
    trustManagerFactory.init(keyStore);
    return trustManagerFactory;
  }

  private KeyManagerFactory initKeyManagerFactory(KeyStore keyStore) throws NoSuchAlgorithmException, UnrecoverableKeyException, KeyStoreException {
    KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
    keyManagerFactory.init(keyStore, KeyStoreUtil.KEY_STORE_PASSWORD);
    return keyManagerFactory;
  }

  private KeyStore createKeyStore(String dockerCertPath) throws GeneralSecurityException, IOException {
    return KeyStoreUtil.createDockerKeyStore(new File(dockerCertPath).getAbsolutePath());
  }
}
