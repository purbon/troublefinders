package com.purbon.kafka.cmd;

import com.purbon.kafka.ssl.KeyStoreEntry;
import com.purbon.kafka.ssl.MTLSServer;
import com.purbon.kafka.ssl.SSLInspector;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import javax.net.ssl.SSLException;
import java.io.FileNotFoundException;
import java.security.KeyStoreException;
import java.util.List;
import java.util.concurrent.Callable;

@Command(name = "store-print",
        mixinStandardHelpOptions = true,
        description = "Display the contents of a keystore as text")
public class KeyStoreAsTextCmd implements Callable<Integer>  {

    @Option(names = {"-k", "--keystore"}, description = "The keystore path")
    private String keyStorePath = "";

    @Option(names = {"-p", "--password"}, description = "The stores password")
    private String password = "";

    @Override
    public Integer call() {
        SSLInspector inspector = new SSLInspector();
        List<KeyStoreEntry> entries;
        try {
            entries = inspector.inspect(keyStorePath, password);
            for(KeyStoreEntry entry : entries) {
                System.out.println(entry.getAlias());
                System.out.println(entry.getCertificate());
            }
        } catch (KeyStoreException | FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        return 0;
    }
}
