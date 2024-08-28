package com.purbon.kafka.ssl;

import javax.net.ssl.*;
import java.io.*;
import java.security.*;
import java.util.Scanner;

public class MTLSClient {

    private final String keyStorePath;
    private final String trustStorePath;
    private final String password;

    public MTLSClient(String keyStorePath,
                      String trustStorePath,
                      String password) {
        this.keyStorePath = keyStorePath;
        this.trustStorePath = trustStorePath;
        this.password = password;
    }

    public void start(String[] enabledProtocols) throws SSLException {
        start("127.0.0.1", 8333, enabledProtocols);
    }

    public void start(String address, int port, String[] enabledProtocols) throws SSLException {
        AppSSLContext appSslContext = new AppSSLContext(keyStorePath, trustStorePath, password);
        SSLContext sslContext = appSslContext.build();

        SSLSocketFactory socketFactory = sslContext.getSocketFactory();
        try (SSLSocket kkSocket = (SSLSocket) socketFactory.createSocket(address, port)) {
            kkSocket.setEnabledProtocols(enabledProtocols);
            PrintWriter out = new PrintWriter(kkSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(kkSocket.getInputStream()));

            int order = 0;
            while(true) {
                String message = Protocol.nextClientMessage(order);
                System.out.println(message);
                if (order == 3) {
                    break;
                }
                out.println(message);
                String reply = in.readLine();
                System.out.println(reply);
                Thread.sleep(1000);
                order += 1;
            }

            in.close();
            out.close();

        } catch (IOException | InterruptedException ex) {
            throw new SSLException(ex);
        }
    }
}
