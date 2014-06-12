package com.pearson.portello.domain;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pearson.model.AuthenticateResponse;
import com.pearson.model.TokenRequest;
import com.pearson.model.WebEntry;
import com.pearson.model.lsEntry;
import com.pearson.model.AuthenticateResponse.AuthParameterType;
import com.pearson.model.AuthenticateResponse.AuthProviderType;
import com.pearson.portello.Constants;
import com.pearson.portello.WSODClient;
import com.pearson.portello.WSODClient.TokenType;

public class SMSLoginProvider implements ILoginProvider {

	@Override
	public AuthenticateResponse login(String userName, String password) {

		AuthenticateResponse authenticateRequest = new AuthenticateResponse();
		
	    List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
	    
	    StringBuffer result =   new StringBuffer();

		urlParameters.add(new BasicNameValuePair("cmd", "login"));
		urlParameters.add(new BasicNameValuePair("siteid", "8313")); // todo check what side id is
		urlParameters.add(new BasicNameValuePair("isCourseAware", "Y"));
		urlParameters.add(new BasicNameValuePair("errurl",
				"http://portalppe.pearsoncmg.com/cclogin.jsp"));
		urlParameters
				.add(new BasicNameValuePair("okurl", "http://portalppe.pearsoncmg.com/portal/Login?externalSiteId=coursecompass"));
		urlParameters.add(new BasicNameValuePair("encPassword", "Y"));
		urlParameters.add(new BasicNameValuePair("loginname", userName));
		urlParameters.add(new BasicNameValuePair("password", md5Java(password)));

		List<NameValuePair> headers = new ArrayList<NameValuePair>();
		headers.add(new BasicNameValuePair("Content-Type",
				"application/x-www-form-urlencoded"));

		HttpResponse response = doPost(
				Constants.PROPERTIES.getProperty("ssoAuthenticateurl"),
				urlParameters, headers , result);
		

		authenticateRequest.autheKey = "Set-Cookie";
		authenticateRequest.authValue = response.getHeaders("Set-Cookie")[0]
				.getValue();
		authenticateRequest.redirect = response.getHeaders("Location")[0]
				.getValue();
		authenticateRequest.authParameterType = AuthParameterType.COOKIE;
		authenticateRequest.provider = AuthProviderType.SMS_SSO;
		authenticateRequest.userid = "11111"; //todo

		
		//load profile 
		
	    urlParameters = new ArrayList<NameValuePair>();
	   	int begin = authenticateRequest.authValue.indexOf("=");
	 	int end = authenticateRequest.authValue.indexOf(";");
	 	String sessionKEy =  authenticateRequest.authValue.substring(begin + 1 ,end) ;
		urlParameters.add(new BasicNameValuePair("key", sessionKEy  ));
		urlParameters.add(new BasicNameValuePair("sec",(UnixCrypt.crypt("PE", sessionKEy)))); // todo check what side id is
		urlParameters.add(new BasicNameValuePair("siteid", "8313"));	
		
		result =   new StringBuffer();
		 
		response = doPost(
				"http://loginppe.pearsoncmg.com/sso/SSOProfileServlet2",
				urlParameters, headers, result);
		
		 readSMSResponse( result.toString() , authenticateRequest);
		
		return  authenticateRequest;
		
	}
	
	private void readSMSResponse(String xmlString, AuthenticateResponse authenticateRequest )
	{
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
				.newInstance();
		DocumentBuilder docBuilder;		

		Document doc;
		
		try {
			docBuilder = docBuilderFactory.newDocumentBuilder();
			doc = docBuilder.parse(new ByteArrayInputStream(xmlString.getBytes()));
			
			doc.getDocumentElement().normalize();
			System.out.println("Root element of the doc is "
					+ doc.getDocumentElement().getNodeName());

			//todo use XPAth , Hemantha
			Node user = doc.getElementsByTagName("User").item(0);			
			Node fname = doc.getElementsByTagName("FirstName").item(0);
			Node lname = doc.getElementsByTagName("LastName").item(0);
						
			
			if (user.getNodeType() == Node.ELEMENT_NODE) {
				Element element = (Element) user;
				authenticateRequest.userid = element.getAttribute("id");
				authenticateRequest.firstName = fname.getTextContent();
				authenticateRequest.lastName = lname.getTextContent();			
			}			
			
		} 
		 catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	
	}

	private String md5Java(String message) {
		String digest = null;
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] hash = md.digest(message.getBytes("UTF-8"));

			// converting byte array to Hexadecimal String
			StringBuilder sb = new StringBuilder(2 * hash.length);
			for (byte b : hash) {
				sb.append(String.format("%02x", b & 0xff));
			}

			digest = sb.toString();

		} catch (UnsupportedEncodingException ex) {
			// Logger.getLogger(StringReplace.class.getName()).log(Level.SEVERE,
			// null, ex);
		} catch (NoSuchAlgorithmException ex) {
			// Logger.getLogger(StringReplace.class.getName()).log(Level.SEVERE,
			// null, ex);
		}
		return digest;
	}

	private HttpResponse doPost(String url,
			List<NameValuePair> urlParameters
			, List<NameValuePair> headers
			, StringBuffer result) {

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

			//StringBuffer result = new StringBuffer();
			String line = "";
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}

//			AuthenticateResponse authenticateRequest = new AuthenticateResponse();
//
//			authenticateRequest.autheKey = "Set-Cookie";
//			authenticateRequest.authValue = response.getHeaders("Set-Cookie")[0]
//					.getValue();
//			authenticateRequest.redirect = response.getHeaders("Location")[0]
//					.getValue();
//			authenticateRequest.authParameterType = AuthParameterType.COOKIE;
//			authenticateRequest.provider = AuthProviderType.SMS_SSO;
//			authenticateRequest.userid = "11111"; //todo
			
			return response;
		}

		catch (UnsupportedEncodingException ex) {

		} catch (ClientProtocolException ex) {

		} catch (IOException ex) {

		}

		return null;
	}

}
