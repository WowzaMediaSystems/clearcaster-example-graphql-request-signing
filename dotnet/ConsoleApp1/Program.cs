using System;

using System.Text;
using System.Threading.Tasks;
using System.Net.Http;
using System.Net.Http.Headers;
using System.Security.Cryptography;

namespace ConsoleApp1
{
    class Program
    {
        private static readonly HttpClient client = new HttpClient();

        private static readonly string CONTENTTYPE_JSON = "application/json";

        private static readonly string key = "[API-Key-Here]";
        private static readonly string secret = "[Secret-Key-Here]";

        private static readonly string url = "https://clearcaster.c2.wowza.com/graphql";
        private static readonly string domain = "clearcaster.c2.wowza.com";
        
        static public string byteArrayToString(byte[] ba)
        {
            StringBuilder hex = new StringBuilder(ba.Length * 2);
            foreach (byte b in ba)
                hex.AppendFormat("{0:x2}", b);
            return hex.ToString();
        }

        static byte[] stringToBytes(string secret)
        {
            var encoding = new System.Text.UTF8Encoding();
            byte[] keyByte = encoding.GetBytes(secret);
            return keyByte;
        }

        static byte[] hmacSHA256(string message, byte[] keyByte)
        {
            var encoding = new System.Text.UTF8Encoding();
            byte[] messageBytes = encoding.GetBytes(message);
            using (var hmacsha256 = new HMACSHA256(keyByte))
            {
                byte[] hashmessage = hmacsha256.ComputeHash(messageBytes);
                return hashmessage;
             }
        }

        private static async Task makeRequest(string url, string domain, string key, string secret)
        {
            string requestStr = "{\"query\":\"query allEncoders { allEncoders { id name } }\"}";

            long requestTime = (long)DateTimeOffset.Now.ToUnixTimeMilliseconds();
            string hmacDigest = byteArrayToString(hmacSHA256(domain, hmacSHA256(requestTime.ToString(), stringToBytes(secret))));
            string authorizationHeader = "HMAC-SHA256, Credential=" + key + ", SignedHeaders=host;x-date, Signature=" + hmacDigest;

            client.DefaultRequestHeaders.TryAddWithoutValidation("Authorization", authorizationHeader);
            client.DefaultRequestHeaders.Add("X-Date", requestTime.ToString());

            client.DefaultRequestHeaders.Accept.Add(new MediaTypeWithQualityHeaderValue(CONTENTTYPE_JSON));

            HttpRequestMessage request = new HttpRequestMessage(HttpMethod.Post, new Uri(url));

            request.Content = new StringContent(requestStr, Encoding.UTF8, CONTENTTYPE_JSON);

            var response = await client.SendAsync(request);
 
            var responseString = await response.Content.ReadAsStringAsync();

            Console.WriteLine("response: \n"+response);
            Console.WriteLine("contents: \n"+responseString);
        }

        static void Main(string[] args)
        {
            try
            {
                makeRequest(url, domain, key, secret).Wait();
            }
            catch (Exception ex)
            {
                Console.WriteLine("There was an exception: {ex.ToString()}");
            } 
        }
    }
}
