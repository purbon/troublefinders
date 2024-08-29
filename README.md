# JDP, Just another deployment helper for Kafka.

Well, not only a deployment helper, but mostly how to get assistant for common problems found during Kafka deployments, 
for example around DNS resolution or SSL certificates acting up.

## Build

To build the tool, please clone the repository and execute the next command:

```bash
 mvn clean package                                                                                                                                                                                                                                                                      ‹system: ruby 2.6.10p210›
[INFO] Scanning for projects...
[INFO] 
[INFO] ---------------< com.purbon.kafka:java-deployment-tools >---------------
[INFO] Building java-deployment-tools 0.0.1
[INFO]   from pom.xml
[INFO] --------------------------------[ jar ]---------------------------------
[INFO] 
[INFO] --- clean:3.2.0:clean (default-clean) @ java-deployment-tools ---
[INFO] Deleting /Users/purbon/work/ps/java-deployment-tools/target

... redacted ...
```
this will create a fat jar, available at the (target)[target/] directory and named java-deployment-tools.

In the future this tool might be delivery in multiple formats, but as of now only the jar deliverable is available.

## How to use JDP

JDP is available as a CLI tool, to use it you can run the following command in your CLI:

```bash
$ java -jar target/java-deployment-tools-0.0.1.jar                                                                                                                                                                                                                                       ‹system: ruby 2.6.10p210›
Usage: deep-tools [COMMAND]
Some tools to help you with troubleshooting deployments
Commands:
  dns-lookup   Simple command to inquiry the DNS.
  tls-server   A TLS server implementing the DNS Haiku protocol
  tls-client   A TLS client implementing the DNS Haiku protocol
  store-print  Display the contents of a keystore as text
  help         Display help information about the specified command.
```

In the previous output you can see the available commands, at the moment of writing this readme, note this 
output might change in the future.

### Available commands

#### DNS Lookup

To easy check how DNS is doing in your infrastructure, you can quickly use this command:

```bash
$ java -jar target/java-deployment-tools-0.0.1.jar dns-lookup -a 142.250.181.206                                                                                                                                                                                                         ‹system: ruby 2.6.10p210›
[INFO ] 2024-08-29 15:14:48.694 [main] DNSLookup - IP Address: 142.250.181.206, Hostname: ham02s21-in-f14.1e100.net
```
```bash
$ java -jar target/java-deployment-tools-0.0.1.jar dns-lookup -a google.com                                                                                                                                                                                                              ‹system: ruby 2.6.10p210›
[INFO ] 2024-08-29 15:15:02.205 [main] DNSLookup - IP Address: 142.250.181.206, Hostname: google.com
```

*Note*, that would be equivalent of using tools like nslookup or dig, but directly using the JVM own APIs.

#### JVM Keystore print

To quickly see the contents of a JVM keystore, you can the following command

```bash
$ java -jar target/java-deployment-tools-0.0.1.jar store-print -k security/certs/fakes/kafka.keystore.jks -p confluent                                                                                                                                                                   ‹system: ruby 2.6.10p210›
kafka
[
[
  Version: V3
  Subject: CN=kafka, OU=TEST, O=CONFLUENT, L=PaloAlto, ST=Ca, C=US
  Signature Algorithm: SHA256withRSA, OID = 1.2.840.113549.1.1.11

  Key:  Sun RSA public key, 2048 bits
  params: null
  modulus: 22130996641583445798934688127058150848011271863193666618960591883422530848221674160232760068432603959559389147073455743287807311030623753929344394184478245707177865970600777351711505534398691263375289400806623023902034697523971933971486984511168071092661415968650557024705196845922410257476132986003491293924494697677146154213163891709273163497660308197526037431521506618508793815939974565935533338799319061189629748680558975367110751591947403960018552695358073177111895633776825391217705310945005046447552401574034278646522797238487919088661892693412782594866230709377876434825431406457277749376139391120688818155753

... redacted ..
```

#### TLS keystore connection check, client-server communication

One of the common cases of troubles is with the keystore's involved in the client, server communication.
With this couple of commands, you will be able to verify the stores are in good shape and nothing is missing, 
for this we have a client and server commands who will talk to each other using a SSL connection.

```bash
$ java -jar target/java-deployment-tools-0.0.1.jar tls-server -h                                                                                                                                                                                                                         ‹system: ruby 2.6.10p210›
Usage: deep-tools tls-server [-hV] [-mtls] [-k=<keyStorePath>] [-p=<password>]
                             [-t=<trustStorePath>]
                             [-protocols=<enabledProtocols>]...
A TLS server implementing the DNS Haiku protocol
  -h, --help      Show this help message and exit.
  -k, --keystore=<keyStorePath>
                  The keystore path
      -mtls
  -p, --password=<password>
                  The stores password
      -protocols=<enabledProtocols>
                  Enabled TLS protocols
  -t, --truststore=<trustStorePath>
                  The truststore path
  -V, --version   Print version information and exit.
```

```bash
$ java -jar target/java-deployment-tools-0.0.1.jar tls-client -h                                                                                                                                                                                                                         ‹system: ruby 2.6.10p210›
Usage: deep-tools tls-client [-hV] [-k=<keyStorePath>] [-p=<password>]
                             [-t=<trustStorePath>]
                             [-protocols=<enabledProtocols>]...
A TLS client implementing the DNS Haiku protocol
  -h, --help      Show this help message and exit.
  -k, --keystore=<keyStorePath>
                  The keystore path
  -p, --password=<password>
                  The stores password
      -protocols=<enabledProtocols>
                  Enabled TLS protocols
  -t, --truststore=<trustStorePath>
                  The truststore path
  -V, --version   Print version information and exit.
```

An example communication would look like:

```bash
$ java -jar target/java-deployment-tools-0.0.1.jar  tls-client  -k security/certs/fakes/kafka.ca1.keystore.jks  -t security/certs/fakes/kafka.ca1.truststore.jks  -p confluent                                                                                                           ‹system: ruby 2.6.10p210›
BOFH we have a problem, can you help me?
It’s not DNS
Are you sure?
There’s no way it’s DNS
Can you ping the machine, please?
It was DNS
Thanks a lot. Bye!
```

```bash
$ java -jar target/java-deployment-tools-0.0.1.jar tls-server -k security/certs/fakes/kafka.ca1.keystore.jks -t security/certs/fakes/kafka.ca1.truststore.jks -p confluent                                                                                                               ‹system: ruby 2.6.10p210›
MTLS: false
Waiting for client connection...
Client connected!
Bye!
Waiting for client connection...
```

Multiple stores are created for tests, each with its own configuration:

* _kafka.keystore.jks_ : Contains only a single self-signed certificate
* _kafka.ca1.keystore.jks_: With a certificate signed with the CA number 1 and the CA number 1 certificate included.
* _kafka.ca1-single.keystore.jks_: A store only with the kafka signed by the CA number 1. *No CA root cert included*. 
* _kafka.ca2.keystore.jks_:  With a certificate signed with the CA number 2 and the CA number 2 certificate included. 
* _kafka.ca1.truststore.jks_: A trust store with the root certificate for CA number 1. 
* _kafka.ca2.truststore.jks_: A trust store with the root certificate for CA number 2.
* _kafka.ca12.truststore.jks_: A trust store with the root certificates for CA number 1 and 2. 

This combinations might help you test multiple possible scenarios beforehand.

## Help 

Feel free to open an issue.
