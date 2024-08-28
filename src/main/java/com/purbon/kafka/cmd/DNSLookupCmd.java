package com.purbon.kafka.cmd;

import com.purbon.kafka.DNSLookup;
import com.purbon.kafka.Tuple;
import picocli.CommandLine.*;

import java.io.IOException;
import java.net.InetAddress;
import java.util.concurrent.Callable;

@Command(name = "dns-lookup",
        mixinStandardHelpOptions = true,
        description = "Simple command to inquiry the DNS.")
public class DNSLookupCmd implements Callable<Integer> {

    @Option(names = {"-a", "--address"}, description = "Hostname or IP to checkup")
    private String address = "127.0.0.1";

    @Override
    public Integer call() throws Exception {
        DNSLookup dnsLookup = new DNSLookup();
        var myAddress = InetAddress.getByName(address);
        Tuple<String> tuple = dnsLookup.verify(myAddress);
        if (tuple.key.equals(tuple.value)) {
            String message = String.format("Hostname: %s is equal to IP Address: %s, this should ideally not be.",
                    tuple.key,
                    tuple.value);
            System.out.println(message);
            return -1;
        }
        return 0;
    }
}
