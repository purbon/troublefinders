package com.purbon.kafka;

import javax.net.ssl.*;
import java.io.*;
import java.security.*;
import java.util.Properties;
import java.util.Scanner;

public class MTLSServer {

    public static void main(String[] args) throws Exception {

        String clientKeyStorePath = "keystore.p12";
        String clientTrustStorePath = "truststore.p12";
        String password = "mystorepassword";

        KeyStore keyStore = loadKeyStore(clientKeyStorePath, password);
        KeyStore trustStore = loadKeyStore(clientTrustStorePath, password);

        X509TrustManager trustManager = setupTrustManager(trustStore, password);
        X509KeyManager keyManager = setupKeyManager(keyStore, password);
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(new KeyManager[]{keyManager}, new TrustManager[]{trustManager}, null);

        SSLServerSocketFactory serverSocketFactory = sslContext.getServerSocketFactory();
        SSLServerSocket serverSocket = (SSLServerSocket) serverSocketFactory.createServerSocket(8333);
        serverSocket.setNeedClientAuth(true);
        serverSocket.setEnabledProtocols(new String[]{"TLSv1.2"});

        while (true) {
            System.out.println("Waiting for client connection...");

            SSLSocket sslSocket = (SSLSocket) serverSocket.accept();
            System.out.println("Client connected!");

            int order = 0;
            var out = new PrintWriter(sslSocket.getOutputStream(), true);
            var in = new BufferedReader(new InputStreamReader(sslSocket.getInputStream()));

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                String response = Protocol.nextServerMessage(order);
                out.println(response);
                if (order == 2) {
                    System.out.println("Bye!");
                    break;
                }
                order += 1;
            }
            sslSocket.close();
        }

    }

    public static X509TrustManager setupTrustManager(KeyStore keyStore, String password) throws KeyStoreException, NoSuchAlgorithmException, NoSuchProviderException {
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

    public static X509KeyManager setupKeyManager(KeyStore keyStore, String password) throws NoSuchAlgorithmException, NoSuchProviderException, UnrecoverableKeyException, KeyStoreException {
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

    public static KeyStore loadKeyStore(String path, String password) {
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
