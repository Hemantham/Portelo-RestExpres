package com.pearson.portello.domain;

import com.pearson.model.AuthenticateResponse;
import com.pearson.model.AuthenticateResponse.AuthProviderType;
import com.strategicgains.restexpress.Request;

public interface ICardLinkProvider {
	
	public String GetLink(Request request);
	
}
