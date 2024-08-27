package com.purbon.kafka;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class DNSLookupTest {

    @Test
    public void testDNSResolution() throws UnknownHostException {

        DNSLookup lookup = new DNSLookup();
        var tuple = lookup.verify(InetAddress.getByName("google.com"));
        Assertions.assertEquals("google.com", tuple.key);
    }
}
