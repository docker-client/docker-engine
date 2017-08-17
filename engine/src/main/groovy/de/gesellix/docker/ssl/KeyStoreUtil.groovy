package de.gesellix.docker.ssl

import groovy.util.logging.Slf4j
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.openssl.PEMKeyPair
import org.bouncycastle.openssl.PEMParser
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter

import java.security.GeneralSecurityException
import java.security.KeyStore
import java.security.KeyStoreException
import java.security.NoSuchAlgorithmException
import java.security.PrivateKey
import java.security.Security
import java.security.cert.Certificate
import java.security.cert.CertificateException
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.security.spec.InvalidKeySpecException

/**
 * A slightly modified copy from https://github.com/rhuss/docker-maven-plugin
 * with kind permission of Roland Huß (https://twitter.com/ro14nd).
 */
@Slf4j
class KeyStoreUtil {

    static {
        if (!Security.getProvider(BouncyCastleProvider.PROVIDER_NAME)) {
            Security.addProvider(new BouncyCastleProvider())
        }
    }

    static KEY_STORE_PASSWORD = "docker".toCharArray()

    static KeyStore createDockerKeyStore(String certPath) throws IOException, GeneralSecurityException {
        PrivateKey privKey = loadPrivateKey(new File(certPath, "key.pem").absolutePath)
        Certificate[] certs = loadCertificates(new File(certPath, "cert.pem").absolutePath)

        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType())
        keyStore.load((KeyStore.LoadStoreParameter) null)

        keyStore.setKeyEntry("docker", privKey, KEY_STORE_PASSWORD, certs)
        addCA(keyStore, new File(certPath, "ca.pem").absolutePath)
        return keyStore
    }

    static PrivateKey loadPrivateKey(String keyPath) throws IOException, GeneralSecurityException {
        PEMParser parser
        try {
            parser = new PEMParser(new FileReader(keyPath))
            Object parsedObject
            while ((parsedObject = parser.readObject()) != null) {
                if (parsedObject instanceof PEMKeyPair) {
                    PEMKeyPair keyPair = (PEMKeyPair) parsedObject
                    return generatePrivateKey(keyPair.getPrivateKeyInfo())
                }
                else if (parsedObject instanceof PrivateKeyInfo) {
                    return generatePrivateKey((PrivateKeyInfo) parsedObject)
                }
            }
        }
        finally {
            if (parser) {
                try {
                    parser.close()
                }
                catch (Exception ignored) {
                    // silently ignored
                }
            }
        }
        throw new GeneralSecurityException("Cannot generate private key from file: " + keyPath)
    }

    static PrivateKey generatePrivateKey(PrivateKeyInfo keyInfo) throws IOException, NoSuchAlgorithmException,
                                                                        InvalidKeySpecException {
        try {
            return new JcaPEMKeyConverter().getPrivateKey(keyInfo)
        }
        catch (InvalidKeySpecException e) {
            log.error("couldn't create private key for asn1oid '${keyInfo.getPrivateKeyAlgorithm().algorithm.id}'", e)
            throw e
        }
    }

    static void addCA(KeyStore keyStore, String caPath) throws KeyStoreException, FileNotFoundException, CertificateException {
        loadCertificates(caPath).each { cert ->
            X509Certificate crt = (X509Certificate) cert
            String alias = crt.subjectX500Principal.name
            keyStore.setCertificateEntry(alias, crt)
        }
    }

    static Collection<Certificate> loadCertificates(String certPath) throws FileNotFoundException, CertificateException {
        InputStream is
        try {
            is = new FileInputStream(certPath)
            return CertificateFactory.getInstance("X509").generateCertificates(is)
        }
        finally {
            if (is) {
                try {
                    is.close()
                }
                catch (Exception ignored) {
                    // silently ignored
                }
            }
        }
    }
}
