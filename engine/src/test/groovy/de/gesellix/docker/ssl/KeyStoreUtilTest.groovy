package de.gesellix.docker.ssl

import spock.lang.Specification

import java.security.GeneralSecurityException
import java.security.KeyStore

class KeyStoreUtilTest extends Specification {

    def "can create KeyStore from RSA certs directory"() {
        when:
        KeyStore keyStore = KeyStoreUtil.createDockerKeyStore(getFile("algorithm/RSA/certpath"))
        KeyStore.PrivateKeyEntry pkEntry = (KeyStore.PrivateKeyEntry) keyStore.getEntry(
                "docker",
                new KeyStore.PasswordProtection(KeyStoreUtil.KEY_STORE_PASSWORD))
        then:
        pkEntry.certificate != null
        keyStore.getCertificate("cn=ca-test,o=internet widgits pty ltd,st=some-state,c=cr")
        keyStore.getCertificate("cn=ca-test-2,o=internet widgits pty ltd,st=some-state,c=cr")
    }

    def "can load RSA/PKCS1"() {
        expect:
        KeyStoreUtil.loadPrivateKey(getFile("algorithm/RSA/keys/pkcs1.pem")) != null
    }

    def "can load RSA/PKCS8"() {
        expect:
        KeyStoreUtil.loadPrivateKey(getFile("algorithm/RSA/keys/pkcs8.pem")) != null
    }

    def "can load ECDSA"() {
        expect:
        KeyStoreUtil.loadPrivateKey(getFile("algorithm/ECDSA/keys/ecdsa.pem")) != null
    }

    def "cannot load invalid key"() {
        when:
        KeyStoreUtil.loadPrivateKey(getFile("algorithm/RSA/keys/invalid.pem"))
        then:
        def exception = thrown(GeneralSecurityException)
        exception.message =~ "Cannot generate private key .*"
    }

    static String getFile(String path) {
        KeyStoreUtilTest.class.getResource(path).getFile()
    }
}
