package com.pearson.portello.domain;

import org.jboss.netty.handler.codec.http.HttpMethod;

import com.pearson.model.AuthenticateResponse;
import com.pearson.model.Course;
import com.pearson.model.TokenRequest;
import com.pearson.model.AuthenticateResponse.AuthParameterType;
import com.pearson.model.AuthenticateResponse.AuthProviderType;
import com.pearson.model.User;
import com.pearson.portello.Constants;
import com.pearson.portello.WSODClient;
import com.pearson.portello.WSODClient.TokenType;

public class WSODLoginProvider implements ILoginProvider {

	@Override
	public AuthenticateResponse login(String userName, String password) {	
		
		String[] userIdString = userName.split("[\\\\]");
		String userId  = userIdString[1];
		String clientName  = userIdString[0];
		
		AuthenticateResponse authenticateRequest = new  AuthenticateResponse();
		WSODClient client = new WSODClient();
		
		String tokenString =  client.getToken(userId, password, clientName ,null, TokenType.PASSWORD);
		
		authenticateRequest.autheKey = "X-Authorization" ;
		authenticateRequest.authParameterType = AuthParameterType.HEADER;		
		authenticateRequest.provider = AuthProviderType.WSOD;	
		authenticateRequest.authValue = "Access_Token access_token=" + tokenString;	
		authenticateRequest.userid = tokenString.split("[|]")[2];
				
		//get user profile
		User user = client.<User, User> CallService(Constants.PROPERTIES.getProperty("mApi")
				+ "/me", null, tokenString
				, HttpMethod.GET
				, User.class
				, WSODClient.RestServiceType.WSOD);
		
		authenticateRequest.firstName = user.firstName;
		authenticateRequest.lastName = user.lastName;
				
		return authenticateRequest;		
	}
}
