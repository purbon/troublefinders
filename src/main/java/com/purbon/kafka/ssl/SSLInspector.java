package com.purbon.kafka.ssl;

import java.io.*;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class SSLInspector {

    public List<KeyStoreEntry> inspect(String keyStorePath, String keyStorePassword) throws KeyStoreException, FileNotFoundException {
        List<KeyStoreEntry> entries = new ArrayList<>();
        InputStream is = new FileInputStream(keyStorePath);
        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        try {
            keyStore.load(is, keyStorePassword.toCharArray());
            Enumeration<String> aliases = keyStore.aliases();
            while(aliases.hasMoreElements()) {
                String alias = aliases.nextElement();
                Certificate certificate = keyStore.getCertificate(alias);
                entries.add(new KeyStoreEntry(alias, certificate));
            }
        } catch (IOException | NoSuchAlgorithmException | CertificateException e) {
            throw new RuntimeException(e);
        }
        return entries;

    }
}
