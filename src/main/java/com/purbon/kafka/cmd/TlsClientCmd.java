package com.purbon.kafka.cmd;

import com.purbon.kafka.ssl.MTLSClient;
import com.purbon.kafka.ssl.MTLSServer;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import javax.net.ssl.SSLException;
import java.util.List;
import java.util.concurrent.Callable;

@Command(name = "tls-client",
        mixinStandardHelpOptions = true,
        description = "A TLS client implementing the DNS Haiku protocol")
public class TlsClientCmd implements Callable<Integer>  {

    @Option(names = {"-k", "--keystore"}, description = "The keystore path")
    private String keyStorePath = "";

    @Option(names = {"-t", "--truststore"}, description = "The truststore path")
    private String trustStorePath = "";

    @Option(names = {"-p", "--password"}, description = "The stores password")
    private String password = "";

    @Option(names = "-protocols", description = "Enabled TLS protocols", defaultValue = "TLSv1.2" )
    List<String> enabledProtocols;


    @Override
    public Integer call() {

        MTLSClient client = new MTLSClient(keyStorePath, trustStorePath, password);
        try {
            client.start(enabledProtocols.toArray(new String[0]));
        } catch (SSLException e) {
            System.out.println(e);
            return -1;
        }
        return 0;
    }
}
