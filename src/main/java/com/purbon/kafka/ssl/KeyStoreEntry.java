package com.purbon.kafka.ssl;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.security.cert.Certificate;

@Data
@AllArgsConstructor
public class KeyStoreEntry {

    private String alias;
    private Certificate certificate;
}
