#!/usr/bin/env bash

CA_PATH=$( dirname ${BASH_SOURCE[0]})

i="kafka"
DEFAULT_DAYS=1825
days="${2:-$DEFAULT_DAYS}"
CN="${3:-$i}"

keytool -genkey -noprompt \
			 -alias $i \
			 -dname "CN=$CN,OU=TEST,O=CONFLUENT,L=PaloAlto,S=Ca,C=US" \
                         -ext "SAN=dns:$i,dns:localhost" \
			 -keystore $i.keystore.jks \
			 -keyalg RSA \
			 -storepass confluent \
			 -keypass confluent \
			 -storetype pkcs12

keytool -genkey -noprompt \
			 -alias $i \
			 -dname "CN=$CN,OU=TEST,O=CONFLUENT,L=PaloAlto,S=Ca,C=US" \
                         -ext "SAN=dns:$i,dns:localhost" \
			 -keystore $i.ca1.keystore.jks \
			 -keyalg RSA \
			 -storepass confluent \
			 -keypass confluent \
			 -storetype pkcs12

keytool -keystore $i.ca1.keystore.jks -alias $i -certreq -file $i.csr -storepass confluent -keypass confluent -ext "SAN=dns:$i,dns:localhost"

DNS_ALT_NAMES=$(printf '%s\n' "DNS.1 = $i.test.local" "DNS.2 = localhost")
#if [[ "$i" == "mds" ]]; then
DNS_ALT_NAMES=$(printf '%s\n' "$DNS_ALT_NAMES" "DNS.3 = broker" "DNS.4 = kafka" "DNS.5 = kafka1" "DNS.6 = kafka2" "DNS.7 = kafka3")
#fi

if [[ "$i" == "thusnelda" ]]; then
DNS_ALT_NAMES=$(printf '%s\n' "DNS.1 = $i" "DNS.2 = localhost")
fi

CERT_SERIAL=$(awk -v seed="$RANDOM" 'BEGIN { srand(seed); printf("0x%.4x%.4x%.4x%.4x\n", rand()*65535 + 1, rand()*65535 + 1, rand()*65535 + 1, rand()*65535 + 1) }')
openssl x509 -req -CA ${CA_PATH}/snakeoil-ca-1.crt -CAkey ${CA_PATH}/snakeoil-ca-1.key -in $i.csr -out $i-ca1-signed.crt -sha256 -days $days -set_serial ${CERT_SERIAL} -passin pass:confluent -extensions v3_req -extfile <(cat <<EOF
[req]
distinguished_name = req_distinguished_name
x509_extensions = v3_req
prompt = no
[req_distinguished_name]
CN = $CN
[v3_req]
extendedKeyUsage = serverAuth, clientAuth
subjectAltName = @alt_names
[alt_names]
$DNS_ALT_NAMES
EOF
)

CERT_SERIAL=$(awk -v seed="$RANDOM" 'BEGIN { srand(seed); printf("0x%.4x%.4x%.4x%.4x\n", rand()*65535 + 1, rand()*65535 + 1, rand()*65535 + 1, rand()*65535 + 1) }')
openssl x509 -req -CA ${CA_PATH}/snakeoil-ca-2.crt -CAkey ${CA_PATH}/snakeoil-ca-2.key -in $i.csr -out $i-ca2-signed.crt -sha256 -days $days -set_serial ${CERT_SERIAL} -passin pass:confluent -extensions v3_req -extfile <(cat <<EOF
[req]
distinguished_name = req_distinguished_name
x509_extensions = v3_req
prompt = no
[req_distinguished_name]
CN = $CN
[v3_req]
extendedKeyUsage = serverAuth, clientAuth
subjectAltName = @alt_names
[alt_names]
$DNS_ALT_NAMES
EOF
)

keytool -noprompt -keystore $i.ca1.keystore.jks -alias CARoot -import -file ${CA_PATH}/snakeoil-ca-1.crt -storepass confluent -keypass confluent
keytool -noprompt -keystore $i.ca1.keystore.jks -alias $i -import -file $i-ca1-signed.crt -storepass confluent -keypass confluent -ext "SAN=dns:$i,dns:localhost"
keytool -noprompt -keystore $i.ca1-single.keystore.jks -alias $i -import -file $i-ca1-signed.crt -storepass confluent -keypass confluent -ext "SAN=dns:$i,dns:localhost"

keytool -noprompt -keystore $i.ca2.keystore.jks -alias CARoot -import -file ${CA_PATH}/snakeoil-ca-2.crt -storepass confluent -keypass confluent
keytool -noprompt -keystore $i.ca2.keystore.jks -alias $i -import -file $i-ca2-signed.crt -storepass confluent -keypass confluent -ext "SAN=dns:$i,dns:localhost"

keytool -noprompt -keystore $i.ca1.truststore.jks -alias CARoot -import -file ${CA_PATH}/snakeoil-ca-1.crt -storepass confluent -keypass confluent
keytool -noprompt -keystore $i.ca2.truststore.jks -alias CARoot -import -file ${CA_PATH}/snakeoil-ca-2.crt -storepass confluent -keypass confluent
keytool -noprompt -keystore $i.ca12.truststore.jks -alias CARoot1 -import -file ${CA_PATH}/snakeoil-ca-1.crt -storepass confluent -keypass confluent
keytool -noprompt -keystore $i.ca12.truststore.jks -alias CARoot2 -import -file ${CA_PATH}/snakeoil-ca-2.crt -storepass confluent -keypass confluent
