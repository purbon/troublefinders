package com.purbon.kafka;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.InetAddress;

public class DNSLookup {

    private static final Logger logger = LogManager.getLogger("DNSLookup");

    public DNSLookup() {

    }

    public Tuple<String> verify(InetAddress address) {
        String hostAddress = address.getHostAddress();
        String hostName = address.getHostName();
        logger.info("IP Address: {}, Hostname: {}", hostAddress, hostName);
        return new Tuple<>(hostName, hostAddress);
    }



    public static void main(String [] args) throws IOException {

        if (args.length != 1) {
            throw new IllegalArgumentException("Unexpected usage: DNSLookup [hostname|ip]");
        }
        String arg = args[0]; //first argument

        DNSLookup dnsLookup = new DNSLookup();
        var myAddress = InetAddress.getByName(arg);
        var tuple = dnsLookup.verify(myAddress);
        if (tuple.key.equals(tuple.value)) {
            String message = String.format("Hostname: %s is equal to IP Address: %s, this should ideally not be.",
                    tuple.key,
                    tuple.value);
            logger.info(message);
            throw new IOException(message);
        }
    }
}
