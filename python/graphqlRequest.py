import requests
import time
import hashlib
import hmac
import base64
import binascii

# pip install requests

CONTENTTYPE_JSON = "application/json"
GRAPHAPI_DATALIMIT = 1024*1024*1024
HTTP_CONNECTIONTIMEOUT = 1000
HTTP_READWRITETIMEOUT = 3000
METHOD_POST = "POST"
CHARACTER_ENCODING = "utf-8"

key = "[API-Key-Here]"
secret = "[Secret-Key-Here]"

url = "https://clearcaster.c2.wowza.com/graphql"
domain = "clearcaster.c2.wowza.com"

requestStr = "{\"query\":\"query allEncoders { allEncoders { id name } }\"}"

currTime = int(round(time.time() * 1000))

currTimeBytes = bytes(str(currTime)).encode(CHARACTER_ENCODING)
secretBytes = bytes(secret).encode(CHARACTER_ENCODING)
domainBytes = bytes(domain).encode(CHARACTER_ENCODING)

sig1 = hmac.new(secretBytes, currTimeBytes, digestmod=hashlib.sha256).digest()
sig2 = hmac.new(sig1, domainBytes, digestmod=hashlib.sha256).digest()

authorizationHeader = "HMAC-SHA256, Credential=" + key + ", SignedHeaders=host;x-date, Signature=" + binascii.hexlify(sig2)

headers = {
	'Authorization': authorizationHeader,
	'X-Date': str(currTime),
	'Content-Type': CONTENTTYPE_JSON,
	'Accept': CONTENTTYPE_JSON
	}

response = requests.post(url = url, headers = headers, data = requestStr)

print(response.text)
