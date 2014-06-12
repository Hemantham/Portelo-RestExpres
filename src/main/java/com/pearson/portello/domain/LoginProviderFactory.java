package com.pearson.portello.domain;

import com.pearson.clients.subpub.Message;
import com.pearson.model.AuthenticateResponse.AuthProviderType;
import com.pearson.portello.Constants;
import com.pearson.portello.serialization.ResponseProcessors;
import com.strategicgains.restexpress.exception.ServiceException;
import com.strategicgains.restexpress.serialization.Serializer;

/**
 * A Singleton factory that creates SubPub message instances, setting necessary context tags
 * to enable publishing.  Messages are serialized to JSON by default, but this can be changed
 * via a call to setSerializer().
 * <p/>
 * By default, the serializer is set to ResponseProcessors.json().getSerializer().
 */
public class LoginProviderFactory
{

	private static final LoginProviderFactory INSTANCE = new LoginProviderFactory();


	
    private LoginProviderFactory()
    {
	    super();
	  
    }
    
    public static LoginProviderFactory instance()
    {
    	return INSTANCE;
    } 
    
    public ILoginProvider Get(AuthProviderType providerType)
    {
    	switch (providerType)
		{		
		case SMS_SSO :			
			return new SMSLoginProvider();			
		case WSOD : 			
			return new  WSODLoginProvider();			
		default : return null;		
		}		
    }
   
   
}
