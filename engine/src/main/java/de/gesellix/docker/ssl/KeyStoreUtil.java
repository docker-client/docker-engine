package de.gesellix.docker.ssl;

import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.util.Collection;
import java.util.Collections;

/**
 * A slightly modified copy from https://github.com/rhuss/docker-maven-plugin
 * with kind permission of Roland Huss (https://twitter.com/ro14nd).
 */
public class KeyStoreUtil {

  private static final Logger log = LoggerFactory.getLogger(KeyStoreUtil.class);

  static {
    if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
      Security.addProvider(new BouncyCastleProvider());
    }
  }

  public static final char[] KEY_STORE_PASSWORD = "docker".toCharArray();

  public static KeyStore createDockerKeyStore(String certPath) throws IOException, GeneralSecurityException {
    PrivateKey privKey = loadPrivateKey(new File(certPath, "key.pem").getAbsolutePath());
    Certificate[] certs = loadCertificates(new File(certPath, "cert.pem").getAbsolutePath()).toArray(new Certificate[] {});

    KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
    keyStore.load(null);

    keyStore.setKeyEntry("docker", privKey, KEY_STORE_PASSWORD, certs);
    addCA(keyStore, new File(certPath, "ca.pem").getAbsolutePath());
    return keyStore;
  }

  public static PrivateKey loadPrivateKey(String keyPath) throws IOException, GeneralSecurityException {
    try (PEMParser parser = new PEMParser(new FileReader(keyPath))) {
      Object parsedObject;
      while ((parsedObject = parser.readObject()) != null) {
        if (parsedObject instanceof PEMKeyPair) {
          PEMKeyPair keyPair = (PEMKeyPair) parsedObject;
          return generatePrivateKey(keyPair.getPrivateKeyInfo());
        }
        else if (parsedObject instanceof PrivateKeyInfo) {
          return generatePrivateKey((PrivateKeyInfo) parsedObject);
        }
      }
    }
    throw new GeneralSecurityException("Cannot generate private key from file: " + keyPath);
  }

  public static PrivateKey generatePrivateKey(final PrivateKeyInfo keyInfo) throws IOException {
    try {
      return new JcaPEMKeyConverter().getPrivateKey(keyInfo);
    }
    catch (Exception e) {
      if (e.getCause() instanceof InvalidKeySpecException) {
        log.error("couldn't create private key for asn1oid '" + keyInfo.getPrivateKeyAlgorithm().getAlgorithm().getId() + "'", e.getCause());
      }
      throw e;
    }
  }

  public static void addCA(final KeyStore keyStore, String caPath) throws KeyStoreException, CertificateException {
    for (Certificate cert : loadCertificates(caPath)) {
      X509Certificate crt = (X509Certificate) cert;
      String alias = crt.getSubjectX500Principal().getName();
      keyStore.setCertificateEntry(alias, crt);
    }
  }

  public static Collection<? extends Certificate> loadCertificates(String certPath) throws CertificateException {
    try (InputStream is = new FileInputStream(certPath)) {
      return CertificateFactory.getInstance("X509").generateCertificates(is);
    }
    catch (IOException ignored) {
      // silently ignored
      return Collections.emptyList();
    }
  }
}
