package com.pearson.portello.domain;

import com.pearson.clients.subpub.Message;
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
public class MessageFactory
{
	// The OpenClass Client ID
	private static final String CLIENT_ID = "berlin";
	
	// The OpenClass Client String
	private static final String CLIENT_STRING = "berlin";

	private static final MessageFactory INSTANCE = new MessageFactory(ResponseProcessors.json().getSerializer());

	private Serializer serializer;
	private String contentType;
	
    private MessageFactory(Serializer serializer)
    {
	    super();
	    setSerializer(serializer);
    }
    
    public static MessageFactory instance()
    {
    	return INSTANCE;
    }
    
    public void setSerializer(Serializer serializer)
    {
    	this.serializer = serializer;
    	String[] parts = serializer.getResultingContentType().split("\\s*;\\s*");
    	this.contentType = parts[0];
    }

    /**
     * Create a SubPub Message instance from a domain object.
     * 
     * @param o a domain object.
     * @return a publishable SubPub Message instance.
     */
	public Message toMessage(Object o)
    {
    	Message m = new Message();
    	m.setSystem(Constants.SUBPUB_SYSTEM);
    	m.setSubSystem(Constants.SUBPUB_SUB_SYSTEM);
    	m.setPayloadString(serializer.serialize(o));
		m.setPayloadContentType(contentType);
		m.setClient(CLIENT_ID);
		m.setClientString(CLIENT_STRING);

		try
        {
	        addContextTags(o, m);
        }
        catch (Exception e)
        {
        	throw new ServiceException(e);
        }

    	return m;
    }

	private void addContextTags(Object o, Message m)
	throws Exception
	{
		if (Taggable.class.isAssignableFrom(o.getClass()))
		{
			((Taggable)o).addContextTags(m);
		}
	}
}
