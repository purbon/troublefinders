package com.purbon.kafka.ssl;

import com.purbon.kafka.App;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.*;

public class AppSSLContext {

    private final String keyStorePath;
    private final String turstStorePath;
    private final String password;

    public AppSSLContext(
            String keyStore,
            String trustStore,
            String password
    ) {
        this.keyStorePath = keyStore;
        this.turstStorePath = trustStore;
        this.password = password;
    }

    public SSLContext build() throws SSLException {
        KeyStore keyStore = loadKeyStore(keyStorePath, password);
        KeyStore trustStore = loadKeyStore(turstStorePath, password);

        try {
            X509TrustManager trustManager = setupTrustManager(trustStore, password);
            X509KeyManager keyManager = setupKeyManager(keyStore, password);
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(new KeyManager[]{keyManager}, new TrustManager[]{trustManager}, null);
            return sslContext;
        } catch (Exception e) {
            throw new SSLException(e);
        }
    }

    public X509TrustManager setupTrustManager(KeyStore keyStore, String password) throws KeyStoreException, NoSuchAlgorithmException, NoSuchProviderException {
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("PKIX", "SunJSSE");
        trustManagerFactory.init(keyStore);
        X509TrustManager x509TrustManager = null;
        for (TrustManager trustManager : trustManagerFactory.getTrustManagers()) {
            if (trustManager instanceof X509TrustManager) {
                x509TrustManager = (X509TrustManager) trustManager;
                break;
            }
        }
        return x509TrustManager;
    }

    public X509KeyManager setupKeyManager(KeyStore keyStore, String password) throws NoSuchAlgorithmException, NoSuchProviderException, UnrecoverableKeyException, KeyStoreException {
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509", "SunJSSE");
        keyManagerFactory.init(keyStore, password.toCharArray());
        X509KeyManager x509KeyManager = null;
        for (KeyManager keyManager : keyManagerFactory.getKeyManagers()) {
            if (keyManager instanceof X509KeyManager) {
                x509KeyManager = (X509KeyManager) keyManager;
                break;
            }
        }
        return x509KeyManager;
    }

    public KeyStore loadKeyStore(String path, String password) {
        try {
            KeyStore keyStore = KeyStore.getInstance("PKCS12");

            InputStream inputStream = new FileInputStream(path);
            keyStore.load(inputStream, password.toCharArray());
            return keyStore;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
