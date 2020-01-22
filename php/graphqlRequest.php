<html>
<head>
<title>Wowza ClearCaster GraphQL PHP Test</title>
</head>
<body>
<?php

$key = "[API-Key-Here]";
$secret = "[Secret-Key-Here]";

$url = "https://clearcaster.c2.wowza.com/graphql";
$domain = "clearcaster.c2.wowza.com";

$queries = <<<'QUERIES'
query allEncoders {
    allEncoders {
        id
        name
    }
}
QUERIES;

function makeRequest($url, $domain, $accessKey, $secretKey, $queries, $operationName = null, $variables = null)
{
    $body = "{";

    if (isset($queries))
    {
        $queriesFixed = str_replace("\n", " ", $queries);
        $queriesFixed = str_replace("\t", " ", $queriesFixed);
        $body .= "\"query\":\"" . $queriesFixed . "\",";
    }
    
    if (isset($operationName))
        $body .= "\"operationName\":\"" . $operationName . "\",";
    
    if (isset($variables))
        $body .= "\"variables\":" . json_encode($variables) . ",";

    $body = substr($body, 0, -1);

    $body .= "}";

    $ch = curl_init($url);

    curl_setopt($ch, CURLOPT_POST, 1);
    curl_setopt($ch, CURLOPT_POSTFIELDS, $body);
    curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);

    $milliseconds = (string)round(microtime(true) * 1000);

    $tmp = hash_hmac('sha256', $milliseconds, $secretKey);
    $signature = hash_hmac('sha256', $domain, hex2bin($tmp));
    $authHeader = 'HMAC-SHA256, Credential=' . $accessKey . ',  SignedHeaders=host;x-date, Signature=' . $signature;
    
    curl_setopt($ch, CURLOPT_HTTPHEADER, array(
        'Content-Type: application/json',
        'Authorization: ' . $authHeader,
        'X-Date: ' . $milliseconds
    ));
    
    $response = curl_exec($ch);
    
    return $response;
}

$variables = array();

$response = makeRequest($url, $domain, $key, $secret, $queries, "allEncoders", $variables);

echo "RESPONSE[allEncoders]: " . $response . "<br/><br/>";

?>
</body>
</html>