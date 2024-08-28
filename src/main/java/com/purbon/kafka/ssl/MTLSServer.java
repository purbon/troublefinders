package com.purbon.kafka.ssl;

import javax.net.ssl.*;
import java.io.*;
import java.security.*;
import java.util.Properties;
import java.util.Scanner;

public class MTLSServer {


    private final String keyStorePath;
    private final String trustStorePath;
    private final String password;

    public MTLSServer(String keyStorePath,
                      String trustStorePath,
                      String password) {
        this.keyStorePath = keyStorePath;
        this.trustStorePath = trustStorePath;
        this.password = password;
    }

    public void start(String[] enabledProtocols, boolean clientAuthRequired) throws SSLException {
        start(8333, enabledProtocols, clientAuthRequired);
    }
    public void start(int port, String[] enabledProtocols, boolean clientAuthRequired) throws SSLException {
        if (enabledProtocols.length == 0) {
            enabledProtocols = new String[]{"TLSv1.2"};
        }
        AppSSLContext appSslContext = new AppSSLContext(keyStorePath, trustStorePath, password);
        SSLContext sslContext = appSslContext.build();

        SSLServerSocketFactory serverSocketFactory = sslContext.getServerSocketFactory();
        try (SSLServerSocket serverSocket = (SSLServerSocket) serverSocketFactory.createServerSocket(port)) {
            serverSocket.setNeedClientAuth(clientAuthRequired);
            serverSocket.setEnabledProtocols(enabledProtocols);

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
            }
        } catch (IOException ex) {
            throw new SSLException(ex);
        }

    }
}
