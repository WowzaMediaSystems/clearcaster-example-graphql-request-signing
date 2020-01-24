import urllib.request
import urllib.parse
import time
import hashlib
import hmac

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
data = requestStr.encode('ascii')

currTime = int(round(time.time() * 1000))

currTimeBytes = bytes(str(currTime), encoding=CHARACTER_ENCODING)
secretBytes = bytes(secret, encoding=CHARACTER_ENCODING)
domainBytes = bytes(domain, encoding=CHARACTER_ENCODING)

sig1 = hmac.new(secretBytes, currTimeBytes, digestmod=hashlib.sha256).digest()
sig2 = hmac.new(sig1, domainBytes, digestmod=hashlib.sha256).digest()

authorizationHeader = "HMAC-SHA256, Credential=" + key + ", SignedHeaders=host;x-date, Signature=" + sig2.hex()

headers = {
	'Authorization': authorizationHeader,
	'X-Date': str(currTime),
	'Content-Type': CONTENTTYPE_JSON,
	'Accept': CONTENTTYPE_JSON
	}

req = urllib.request.Request(url, data, headers)
with urllib.request.urlopen(req) as response:
   print(response.read().decode(CHARACTER_ENCODING))
