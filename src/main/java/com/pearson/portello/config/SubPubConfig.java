package com.pearson.portello.config;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pearson.clients.subpub.SubPubMessageBus;
import com.pearson.clients.subpub.SubPubPrincipal;
import com.strategicgains.restexpress.exception.ConfigurationException;

public class SubPubConfig
{
	private static final Logger LOG = LoggerFactory.getLogger(SubPubConfig.class);

	private static final String SUBPUB_IS_ENABLED_PROPERTY = "subpub.isEnabled";
	private static final String SUBPUB_HOST_PROPERTY = "subpub.host";
	private static final String SUBPUB_PORT_PROPERTY = "subpub.port";
	private static final String SUBPUB_KEY_PROPERTY = "subpub.key";
	private static final String SUBPUB_PRINCIPAL_ID_PROPERTY = "subpub.principalId";
	private static final String SUBPUB_CALLBACK_BASE_URL = "subpub.callbackBaseUrl";

	private SubPubMessageBus bus;

    public SubPubConfig(Properties p)
    {
    	if (!Boolean.parseBoolean(p.getProperty(SUBPUB_IS_ENABLED_PROPERTY, "false")))
    	{
    		LOG.warn("********* SubPub Not Enabled *********");
    		this.bus = null;
    		return;
    	}

    	String subpubHost = p.getProperty(SUBPUB_HOST_PROPERTY);
    	
    	if (subpubHost == null)
    	{
    		throw new ConfigurationException("Please configure a SubPub host: " + SUBPUB_HOST_PROPERTY);
    	}

    	int subpubPort;

    	try
    	{
    		subpubPort = Integer.parseInt(p.getProperty(SUBPUB_PORT_PROPERTY));
    	}
    	catch(NumberFormatException e)
    	{
    		throw new ConfigurationException("Please configure a SubPub port: " + SUBPUB_PORT_PROPERTY, e);
    	}

    	SubPubPrincipal principal = new SubPubPrincipal();
    	principal.setKey(p.getProperty(SUBPUB_KEY_PROPERTY));
    	
    	if (principal.getKey() == null)
    	{
    		throw new ConfigurationException("Please configura a PubSub key: " + SUBPUB_KEY_PROPERTY);
    	}
    	
    	principal.setPrincipalId(p.getProperty(SUBPUB_PRINCIPAL_ID_PROPERTY));
    	
    	if (principal.getPrincipalId() == null)
    	{
    		throw new ConfigurationException("Please configure a SubPub PrincipalId: " + SUBPUB_PRINCIPAL_ID_PROPERTY);
    	}
    	
    	String callbackBaseUrl = p.getProperty(SUBPUB_CALLBACK_BASE_URL);
    	
    	if (callbackBaseUrl == null)
    	{
    		throw new ConfigurationException("Please configure a SubPub callback base URL: " + SUBPUB_CALLBACK_BASE_URL);
    	}
    	
    	this.bus = new SubPubMessageBus(subpubHost, subpubPort, callbackBaseUrl, principal);
    }
    
    public SubPubMessageBus getBus()
    {
    	return bus;
    }
}
