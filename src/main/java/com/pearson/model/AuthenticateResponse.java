package com.pearson.model;


public class AuthenticateResponse {	
	
		public String autheKey;
		public String authValue ;
		public AuthParameterType authParameterType ;
		public AuthProviderType provider ;
		public String userid ;
		public String redirect;
		public String firstName;
		public String lastName;
		
public enum AuthProviderType {
			SMS_SSO
			,WSOD 
			
		}
public enum AuthParameterType {
	HEADER
	,QUERYSTRING
	,COOKIE
	,BODY
	
}
}


