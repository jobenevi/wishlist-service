#!/bin/bash

# Extract secret from application.properties
SECRET=$(grep '^spring.security.oauth2.resourceserver.jwt.secret=' src/main/resources/application-local.properties | cut -d'=' -f2)

if [ -z "$SECRET" ]; then
  echo "JWT secret not found in application.properties"
  exit 1
fi

USER_ID="${1:-1}"
HEADER='{"alg":"HS256","typ":"JWT"}'
PAYLOAD="{\"sub\":\"$USER_ID\",\"user_id\":\"$USER_ID\",\"scope\":\"user\",\"iat\":$(date +%s),\"exp\":$(( $(date +%s) + 3600 ))}"

base64url() {
  echo -n "$1" | openssl base64 -A | tr '+/' '-_' | tr -d '='
}

HEADER_B64=$(base64url "$HEADER")
PAYLOAD_B64=$(base64url "$PAYLOAD")
SIGNING_INPUT="$HEADER_B64.$PAYLOAD_B64"
SIGNATURE=$(echo -n "$SIGNING_INPUT" | openssl dgst -sha256 -hmac "$SECRET" -binary | openssl base64 -A | tr '+/' '-_' | tr -d '=')

JWT="$SIGNING_INPUT.$SIGNATURE"

echo "$JWT"
