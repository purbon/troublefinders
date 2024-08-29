package com.purbon.kafka.cmd;

import com.purbon.kafka.ssl.MTLSServer;
import picocli.CommandLine;
import picocli.CommandLine.*;

import javax.net.ssl.SSLException;
import java.util.List;
import java.util.concurrent.Callable;

@Command(name = "tls-server",
        mixinStandardHelpOptions = true,
        description = "A TLS server implementing the DNS Haiku protocol")
public class TlsServerCmd implements Callable<Integer>  {

    @Option(names = {"-k", "--keystore"}, description = "The keystore path")
    private String keyStorePath = "";

    @Option(names = {"-t", "--truststore"}, description = "The truststore path")
    private String trustStorePath = "";

    @Option(names = {"-p", "--password"}, description = "The stores password")
    private String password = "";

    @Option(names = "-mtls", defaultValue = "false")
    boolean mtls;

    @Option(names = "-protocols", description = "Enabled TLS protocols", defaultValue = "TLSv1.2" )
    List<String> enabledProtocols;


    @Override
    public Integer call() {

        MTLSServer server = new MTLSServer(keyStorePath, trustStorePath, password);
        try {
            System.out.println("MTLS: "+mtls);
            server.start(enabledProtocols.toArray(new String[0]), mtls);
        } catch (SSLException e) {
            e.printStackTrace();
            return -1;
        }
        return 0;
    }
}
