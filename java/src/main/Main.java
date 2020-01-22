package main;

import java.io.*;
import java.net.*;
import java.util.*;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class Main
{
	private static final String CONTENTTYPE_JSON = "application/json";	
	private static final int GRAPHAPI_DATALIMIT = 1024*1024*1024;
	private static final int HTTP_CONNECTIONTIMEOUT = 1000;
	private static final int HTTP_READWRITETIMEOUT = 3000;
	private static final String METHOD_POST = "POST";
	private static final String CHARACTER_ENCODING = "UTF-8";

	private static final String key = "[API-Key-Here]";
	private static final String secret = "[Secret-Key-Here]";
	
	private static final String url = "https://clearcaster.c2.wowza.com/graphql";
	private static final String domain = "clearcaster.c2.wowza.com";

	private static String bytesToHex(byte[] bytes)
	{
		final char[] hexArray = "0123456789ABCDEF".toCharArray();
		char[] hexChars = new char[bytes.length * 2];
		for (int j = 0; j < bytes.length; j++)
		{
			int v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}

		return new String(hexChars);
	}

	private static String generateSignature(long epochMills, String host, String secretKey)
	{
		Mac sha256_HMAC = null;
		String signature = null;

		try
		{
			byte[] byteSecret = secretKey.getBytes("UTF-8");
			final String HMAC_SHA256 = "HmacSHA256";
			sha256_HMAC = Mac.getInstance(HMAC_SHA256);
			SecretKeySpec keySpec = new SecretKeySpec(byteSecret, HMAC_SHA256);
			sha256_HMAC.init(keySpec);
			byte[] mac_data = sha256_HMAC.doFinal(String.valueOf(epochMills).getBytes("UTF-8"));
			SecretKeySpec hashedKeySpec = new SecretKeySpec(mac_data, HMAC_SHA256);
			sha256_HMAC.init(hashedKeySpec);
			byte[] finalMacData = sha256_HMAC.doFinal(host.getBytes("UTF-8"));
			signature = bytesToHex(finalMacData);
		}
		catch (Exception e)
		{
			System.out.println("Main.generateSignature[hmac]: " + e);
		}

		return signature;
	}

	private static void makeRequest(String url, String domain, String key, String secret)
	{
		String requestStr = "{\"query\":\"query allEncoders { allEncoders { id name } }\"}";
		
		URL uri= null;
		try
		{
			uri = new URL(url);
		}
		catch(Exception e)
		{
			System.out.println("Main.makeRequest[url]: "+e);
		}

		long currTime =  System.currentTimeMillis();

		String requestSignature = generateSignature(currTime, domain, secret);
		String payloadStr = null;

		try
		{
			HttpURLConnection httpConnection = (HttpURLConnection)uri.openConnection();
						
			httpConnection.setRequestMethod(METHOD_POST);
			httpConnection.setDoInput(true);
			httpConnection.setDoOutput(true);
			httpConnection.setUseCaches(false);
			
			httpConnection.setConnectTimeout(HTTP_CONNECTIONTIMEOUT);
			httpConnection.setReadTimeout(HTTP_READWRITETIMEOUT);

			httpConnection.setRequestProperty("Content-Type", CONTENTTYPE_JSON);
			httpConnection.setRequestProperty("Authorization", "HMAC-SHA256, Credential="+key+", SignedHeaders=host;x-date, Signature="+requestSignature);
			httpConnection.setRequestProperty("X-Date", String.valueOf(currTime));
			httpConnection.connect();
			
			DataOutputStream output = new DataOutputStream(httpConnection.getOutputStream());
			if (output != null)
			{
				output.write(requestStr.getBytes("UTF-8"));
				output.flush();
				output.close();
			}

			DataInputStream input = null;

			if (input == null)
			{
				try
				{
					input = new DataInputStream(httpConnection.getInputStream());
				}
				catch(Exception e)
				{
					System.out.println("Main.makeRequest[inputStream]: "+e);
				}
			}

			if (input == null)
			{
				try
				{
					input = new DataInputStream(httpConnection.getErrorStream());
				}
				catch(Exception e)
				{
					System.out.println("Main.makeRequest[errorStream]: "+e);
				}
			}

			int dataLimit = GRAPHAPI_DATALIMIT;
			byte[] payload = null;

			if (input != null)
			{
				ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
				DataOutputStream dataOut = new DataOutputStream(byteOut);

				int rChunk = 64 * 1024;
				byte[] myData = new byte[rChunk];
				long dataSize = 0;

				while (true)
				{
					int bytesRead = input.read(myData, 0, rChunk);
					if (bytesRead == -1)
						break;
					else
					{
						dataSize += bytesRead;
						dataOut.write(myData, 0, bytesRead);
						Thread.sleep(1);
					}

					if (dataLimit > 0 && dataSize >= dataLimit)
						break;
				}

				payload = byteOut.toByteArray();
			}

			if (payload != null)
			{
				payloadStr = new String(payload, CHARACTER_ENCODING);
			}
		}
		catch(Exception e)
		{
			System.out.println("Main.makeRequest[request]: "+e);
		}

		System.out.println(payloadStr);
	}

	public static void main(String[] args)
	{
		makeRequest(url, domain, key, secret);
	}
}