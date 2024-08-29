# Common Java SSL errors

## Problems with the certificate validation chain

In a nutshell, be aware that the required CA certificates are in your trust stores so the SSL handshake can finish 
successfully, otherwise you will see an error like this one:

```bash
$ java -jar target/java-deployment-tools-0.0.1.jar tls-server -k security/certs/fakes/kafka.ca1.keystore.jks -t security/certs/fakes/kafka.ca1.truststore.jks -p confluent -mtls                                                                                                         ‹system: ruby 2.6.10p210›
MTLS: true
Waiting for client connection...
Client connected!
javax.net.ssl.SSLException: javax.net.ssl.SSLHandshakeException: Empty server certificate chain
        at com.purbon.kafka.ssl.MTLSServer.start(MTLSServer.java:61)
        at com.purbon.kafka.ssl.MTLSServer.start(MTLSServer.java:25)
        at com.purbon.kafka.cmd.TlsServerCmd.call(TlsServerCmd.java:38)
        at com.purbon.kafka.cmd.TlsServerCmd.call(TlsServerCmd.java:11)
        at picocli.CommandLine.executeUserObject(CommandLine.java:2045)
        at picocli.CommandLine.access$1500(CommandLine.java:148)
        at picocli.CommandLine$RunLast.executeUserObjectOfLastSubcommandWithSameParent(CommandLine.java:2465)
...
```
```bash
$ java -jar target/java-deployment-tools-0.0.1.jar  tls-client  -k security/certs/fakes/kafka.keystore.jks  -t security/certs/fakes/kafka.ca1.truststore.jks  -p confluent                                                                                                               ‹system: ruby 2.6.10p210›
BOFH we have a problem, can you help me?
javax.net.ssl.SSLException: javax.net.ssl.SSLException: Connection has closed: javax.net.ssl.SSLException: Broken pipe
        at com.purbon.kafka.ssl.MTLSClient.start(MTLSClient.java:55)
        at com.purbon.kafka.ssl.MTLSClient.start(MTLSClient.java:23)
        at com.purbon.kafka.cmd.TlsClientCmd.call(TlsClientCmd.java:35)
        at com.purbon.kafka.cmd.TlsClientCmd.call(TlsClientCmd.java:12)
        at picocli.CommandLine.executeUserObject(CommandLine.java:2045)
....
```

## Unknown certificate

```bash 
$ java -jar target/java-deployment-tools-0.0.1.jar tls-server -k security/certs/fakes/kafka.ca1.keystore.jks -t security/certs/fakes/kafka.ca1.truststore.jks -p confluent                                                                                                               ‹system: ruby 2.6.10p210›
MTLS: false
Waiting for client connection...
Client connected!
Bye!
Waiting for client connection...
Client connected!
Bye!
Waiting for client connection...
Client connected!
javax.net.ssl.SSLException: javax.net.ssl.SSLHandshakeException: Received fatal alert: certificate_unknown
        at com.purbon.kafka.ssl.MTLSServer.start(MTLSServer.java:61)
        at com.purbon.kafka.ssl.MTLSServer.start(MTLSServer.java:25)
        at com.purbon.kafka.cmd.TlsServerCmd.call(TlsServerCmd.java:38)
        at com.purbon.kafka.cmd.TlsServerCmd.call(TlsServerCmd.java:11)
....
```

```bash
$ java -jar target/java-deployment-tools-0.0.1.jar  tls-client  -k security/certs/fakes/kafka.keystore.jks  -t security/certs/fakes/kafka.ca2.truststore.jks  -p confluent                                                                                                               ‹system: ruby 2.6.10p210›
BOFH we have a problem, can you help me?
javax.net.ssl.SSLException: javax.net.ssl.SSLException: Connection has closed: javax.net.ssl.SSLHandshakeException: PKIX path building failed: sun.security.provider.certpath.SunCertPathBuilderException: unable to find valid certification path to requested target
        at com.purbon.kafka.ssl.MTLSClient.start(MTLSClient.java:55)
        at com.purbon.kafka.ssl.MTLSClient.start(MTLSClient.java:23)
        at com.purbon.kafka.cmd.TlsClientCmd.call(TlsClientCmd.java:35)
        at com.purbon.kafka.cmd.TlsClientCmd.call(TlsClientCmd.java:12)
        at picocli.CommandLine.executeUserObject(CommandLine.java:2045)
.... 
```