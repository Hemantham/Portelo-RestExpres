package com.pearson.portello.domain;

import com.pearson.model.AuthenticateResponse;

public interface ILoginProvider {
	
	public AuthenticateResponse login(String userName, String Password);
	
}
