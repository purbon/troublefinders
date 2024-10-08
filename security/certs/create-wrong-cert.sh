#!/usr/bin/env bash

CA_PATH=$( dirname ${BASH_SOURCE[0]})

i=$1
DEFAULT_DAYS=1825
days="${2:-$DEFAULT_DAYS}"
CN="${3:-$i}"
SIGN_BY_CA="${4:-1}"
USE_OK_CA="${5:-1}"

if (( $USE_OK_CA == 1)); then
  CA_FILE="${CA_PATH}/snakeoil-ca-1.crt"
  CA_KEY="${CA_PATH}/snakeoil-ca-1.key"
else
  CA_FILE="${CA_PATH}/snakeoil-ca-2.crt"
  CA_KEY="${CA_PATH}/snakeoil-ca-2.key"
fi
# Create host keystore
keytool -genkey -noprompt \
			 -alias $i \
			 -dname "CN=$CN,OU=TEST,O=CONFLUENT,L=PaloAlto,S=Ca,C=US" \
                         -ext "SAN=dns:$i,dns:localhost" \
			 -keystore $i.keystore.jks \
			 -keyalg RSA \
			 -storepass confluent \
			 -keypass confluent \
			 -storetype pkcs12

# Create the certificate signing request (CSR)
keytool -keystore $i.keystore.jks -alias $i -certreq -file $i.csr -storepass confluent -keypass confluent -ext "SAN=dns:$i,dns:localhost"
#openssl req -in $i.csr -text -noout

# Enables 'confluent login --ca-cert-path /etc/kafka/secrets/snakeoil-ca-1.crt --url https://kafka1:8091'
DNS_ALT_NAMES=$(printf '%s\n' "DNS.1 = $i.test.local" "DNS.2 = localhost")
#if [[ "$i" == "mds" ]]; then
DNS_ALT_NAMES=$(printf '%s\n' "$DNS_ALT_NAMES" "DNS.3 = broker" "DNS.4 = kafka" "DNS.5 = kafka1" "DNS.6 = kafka2" "DNS.7 = kafka3")
#fi

if [[ "$i" == "thusnelda" ]]; then
DNS_ALT_NAMES=$(printf '%s\n' "DNS.1 = $i" "DNS.2 = localhost")
fi

# Sign the host certificate with the certificate authority (CA)
# Set a random serial number (avoid problems from using '-CAcreateserial' when parallelizing certificate generation)
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
#openssl x509 -noout -text -in $i-ca1-signed.crt

# Sign and import the CA cert into the keystore
keytool -noprompt -keystore $i.keystore.jks -alias CARoot -import -file ${CA_PATH}/snakeoil-ca-1.crt -storepass confluent -keypass confluent
#keytool -list -v -keystore $i.keystore.jks -storepass confluent

# Sign and import the host certificate into the keystore
if (( $SIGN_BY_CA == 1)); then
  keytool -noprompt -keystore $i.keystore.jks -alias $i -import -file $i-ca1-signed.crt -storepass confluent -keypass confluent -ext "SAN=dns:$i,dns:localhost"
  #keytool -list -v -keystore $i.keystore.jks -storepass confluent
fi

# Create truststore and import the CA cert
keytool -noprompt -keystore $i.truststore.jks -alias CARoot -import -file $CA_FILE -storepass confluent -keypass confluent
#keytool -noprompt -keystore $i.truststore.jks -alias CARoot -import -file ${CA_PATH}/snakeoil-ca-1.crt -storepass confluent -keypass confluent

# Save creds
echo "confluent" > ${i}_sslkey_creds
echo "confluent" > ${i}_keystore_creds
echo "confluent" > ${i}_truststore_creds

# Create pem files and keys used for Schema Registry HTTPS testing
#   openssl x509 -noout -modulus -in client.certificate.pem | openssl md5
#   openssl rsa -noout -modulus -in client.key | openssl md5
#   echo "GET /" | openssl s_client -connect localhost:8085/subjects -cert client.certificate.pem -key client.key -tls1
keytool -export -alias $i -file $i.der -keystore $i.keystore.jks -storepass confluent
openssl x509 -inform der -in $i.der -out $i.certificate.pem
keytool -importkeystore -srckeystore $i.keystore.jks -destkeystore $i.keystore.p12 -deststoretype PKCS12 -deststorepass confluent -srcstorepass confluent -noprompt
openssl pkcs12 -in $i.keystore.p12 -nodes -nocerts -out $i.key -passin pass:confluent
