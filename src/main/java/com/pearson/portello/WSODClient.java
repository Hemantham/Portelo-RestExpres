package com.pearson.portello;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.bouncycastle.crypto.engines.AESFastEngine;
import org.bouncycastle.crypto.macs.CMac;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.util.Strings;
import org.bouncycastle.util.encoders.Hex;
import org.jboss.netty.handler.codec.http.HttpMethod;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pearson.model.Course;
import com.pearson.model.LinkedCard;
import com.pearson.portello.config.Configuration;

public class WSODClient {

	//Configuration configuration = new Configuration();

	public enum RestServiceType {
		WSOD, RESTExpress
	}

	public enum TokenType {
		PASSWORD, ASSERTION
	}

	private <T> T doPost(String url, List<NameValuePair> urlParameters,
			List<NameValuePair> headers) {

		try {

			HttpClient client = new DefaultHttpClient();

			HttpPost post = new HttpPost(url);

			// add header
			for (NameValuePair header : headers) {
				post.setHeader(header.getName(), header.getValue());
			}

			post.setEntity(new UrlEncodedFormEntity(urlParameters));

			HttpResponse response = client.execute(post);
			System.out.println("Response Code : "
					+ response.getStatusLine().getStatusCode());

			BufferedReader rd = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent()));

			StringBuffer result = new StringBuffer();
			String line = "";
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}

			Gson gson = new Gson();
			Type mapType = new TypeToken<T>() {	}.getType();
			T ser = gson.fromJson(result.toString(), mapType);

			return ser;
		}

		catch (UnsupportedEncodingException ex) {

		} catch (ClientProtocolException ex) {

		} catch (IOException ex) {

		}

		return null;
	}

	// makes a GET POST request with a oauthToken and returns a generic type.
	// Author : Hemantha
	// 18/11/2013
	public <T, POSTTYPE> T CallService(String url, POSTTYPE postObject,
			String username, String password, String clientString,String clientName , 
			HttpMethod method, Class<T> clazz, RestServiceType restServiceType) {
	
			String token = getToken(username, password, clientString, clientName, TokenType.ASSERTION);
			
		    return 	this.< T, POSTTYPE> CallService( url,  postObject,
					token,
					 method, clazz,  restServiceType);			
	}

	// makes a GET POST request with a oauthToken and returns a generic type.
		// Author : Hemantha
		// 18/11/2013
		public <T, POSTTYPE> T CallService(String url, POSTTYPE postObject,
				String token,
				HttpMethod method, Class<T> clazz, RestServiceType restServiceType) {

			try {				

				HttpClient client = new DefaultHttpClient();

				HttpResponse response = null;

				HttpRequestBase request = null;

				Gson gson = new Gson();

				// add header

				if (method == HttpMethod.POST) {
					request = new HttpPost(url);
					String json;
					if (restServiceType == RestServiceType.WSOD) {

						json = "{\"" + postObject.getClass().getSimpleName()
								+ "\" : " + gson.toJson(postObject) + "}";
					} else {
						json = gson.toJson(postObject);
					}

					System.out.println("sending json : " + json);

					StringEntity requestEntity = new StringEntity(json);
					requestEntity.setContentType("application/json");

					((HttpPost) request).setEntity(requestEntity);
				} else {
					request = new HttpGet(url);
				}

				request.setHeader("X-Authorization", "Access_Token access_token="
						+ token);

				response = client.execute(request);

				System.out.println("Response Code : "
						+ response.getStatusLine().getStatusCode());

				BufferedReader rd = new BufferedReader(new InputStreamReader(
						response.getEntity().getContent()));

				StringBuffer result = new StringBuffer();
				String line = "";
				while ((line = rd.readLine()) != null) {
					result.append(line);
				}

				String resultString = result.toString().trim();
				if (restServiceType == RestServiceType.WSOD) {

					resultString = "{"
							+ resultString.substring(resultString.indexOf(":") + 1,
									resultString.length());

					// TODO for post
					resultString = resultString.substring(1,
							resultString.length() - 1);
				}

				System.out.println(resultString);

				// Type mapType2 = new TypeToken<T>() { }.getType();

				T ser = gson.fromJson(resultString, clazz);

				return ser;
			}

			catch (UnsupportedEncodingException ex) {

			} catch (ClientProtocolException ex) {

			} catch (IOException ex) {

			}

			return null;
		}
	
	public String getToken(String username, String password, String clientString,String clientName
			,TokenType tokenType) {

		List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();

		if (tokenType == TokenType.PASSWORD) {

			urlParameters.add(new BasicNameValuePair("grant_type", "password"));
			urlParameters.add(new BasicNameValuePair("client_id",
					Constants.PROPERTIES.getProperty("applicationID")));
			urlParameters.add(new BasicNameValuePair("username", clientString
					+ "\\" + username));
			urlParameters.add(new BasicNameValuePair("password", password));

		} else {			
		
			try
			{
			// Create the Assertion String
			String assertion = buildAssertion(clientName, Constants.PROPERTIES.getProperty("KeyMoniker"),
					Constants.PROPERTIES.getProperty("applicationID"), clientString, username, Constants.PROPERTIES.getProperty("Secret"));
				
				urlParameters.add(new BasicNameValuePair("grant_type", "assertion"));
				urlParameters.add(new BasicNameValuePair("assertion_type",Constants.PROPERTIES.getProperty("assertionType")));
				urlParameters.add(new BasicNameValuePair("assertion", assertion));
				urlParameters.add(new BasicNameValuePair("password", password));
				
			}
			catch(UnsupportedEncodingException ex)
			{
				
			}

		}
		

		List<NameValuePair> headers = new ArrayList<NameValuePair>();
		headers.add(new BasicNameValuePair("Content-Type",
				"application/x-www-form-urlencoded"));

		Map<String, String> response = this.<Map<String, String>> doPost(
				Constants.PROPERTIES.getProperty("authenticateurl"), urlParameters, headers);

		return response.get("access_token");

	}

	private static String buildAssertion(String applicationName,
			String keyMoniker, String applicationID, String clientString,
			String username, String secret) throws UnsupportedEncodingException {
		// Get the UTC Date Timestamp
		DateFormat gmtFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		TimeZone gmtTime = TimeZone.getTimeZone("GMT");
		gmtFormat.setTimeZone(gmtTime);
		String timestamp = gmtFormat.format(new Date()) + "Z";

		// Setup the Assertion String
		String assertion = applicationName + "|" + keyMoniker + "|"
				+ applicationID + "|" + clientString + "|" + username + "|"
				+ timestamp;

		// Generate the CMAC used for Assertion Security
		String cmac = generateCmac(secret, assertion);

		// Add the CMAC to the Assertion String
		assertion = assertion + "|" + cmac;
		System.out.println("Assertion String = " + assertion);
		return assertion;
	}

	private static String generateCmac(String key, String msg)
			throws UnsupportedEncodingException {
		byte[] keyBytes = key.getBytes("UTF-8");
		byte[] data = msg.getBytes("UTF-8");

		CMac macProvider = new CMac(new AESFastEngine());
		macProvider.init(new KeyParameter(keyBytes));
		macProvider.reset();

		macProvider.update(data, 0, data.length);
		byte[] output = new byte[macProvider.getMacSize()];
		macProvider.doFinal(output, 0);

		return Strings.fromUTF8ByteArray(Hex.encode(output));
	}
}
