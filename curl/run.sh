#!/bin/bash

KEY="[API-Key-Here]"
SECRET="[Secret-Key-Here]"

DOMAIN="clearcaster.c2.wowza.com"

DATE=`date +%s000`
TMP=`echo -n "$DATE" | openssl dgst -sha256 -hmac $SECRET -binary`
HMACDIGEST=`echo -n "$DOMAIN" | openssl dgst -sha256 -hmac $TMP`
authHeader="HMAC-SHA256, Credential=$KEY, SignedHeaders=host;x-date, Signature=${HMACDIGEST}"

curl -X POST -H "X-Date: $DATE" -H 'Content-Type: application/json' -H "Authorization: $authHeader" https://clearcaster.c2.wowza.com/graphql --data '{"query":"query allEncoders { allEncoders { id name } }"}'
