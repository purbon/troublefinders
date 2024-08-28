package com.purbon.kafka;

import com.purbon.kafka.ssl.KeyStoreEntry;
import com.purbon.kafka.ssl.SSLInspector;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.security.KeyStoreException;

public class SSLInspectorTest {

    @Test
    public void testKeyStoreLookup() throws FileNotFoundException, KeyStoreException {
        var url = getClass().getResource("/client.keystore.jks");
        SSLInspector inspector = new SSLInspector();
        assert url != null;
        var entries = inspector.inspect(url.getPath(), "confluent");
        
        for(KeyStoreEntry entry : entries) {
            System.out.println(entry.getAlias());
            System.out.println(entry.getCertificate());


        }
    }
}
