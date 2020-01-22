import { execute, makePromise } from 'apollo-link';
import { createHttpLink } from "apollo-link-http";
import { onError } from "apollo-link-error";
import gql from 'graphql-tag'
import hmacSHA256 from 'crypto-js/hmac-sha256';
import Hex from 'crypto-js/enc-hex';
 
global.fetch = require('node-fetch');
 
export const queryAllEncoders = gql`
query allEncoders {
    allEncoders {
        id
        name
    }
}
`
 
let key = "[API-Key-Here]";
let secret = "[Secret-Key-Here]";

let url = "https://clearcaster.c2.wowza.com/graphql";
let domain = "clearcaster.c2.wowza.com";
 
let link = createHttpLink({ uri: url });
 
function getAuthHeader(domain, key, secret)
{
    let headers;
 
    let requestTime = Date.now();
    let hmacDigest = Hex.stringify(hmacSHA256(domain, hmacSHA256(requestTime.toString(), secret)));
    let authorizationHeader =`HMAC-SHA256, Credential=${key}, SignedHeaders=host;x-date, Signature=${hmacDigest}`;
     
    headers = {
        'Authorization': authorizationHeader,
        'X-Date': requestTime
    }
 
    return headers;
}
 
function makeRequest(link, operation)
{
    return makePromise(execute(link, operation));
}
 
function getAllEncoders(link, domain, key, secret)
{
    let authHeader = getAuthHeader(domain, key, secret);
 
    let operation = {
        query: queryAllEncoders,
        variables: {},
        context: {
            headers: authHeader
        }
    };
 
    return makeRequest(link, operation);
}
 
getAllEncoders(link, domain, key, secret).then((response) => {
    console.log(JSON.stringify(response));
});