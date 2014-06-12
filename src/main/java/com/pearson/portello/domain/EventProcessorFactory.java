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
public class EventProcessorFactory
{

	private static final EventProcessorFactory INSTANCE = new EventProcessorFactory();


	
    private EventProcessorFactory()
    {
	    super();
	  
    }
    
    public static EventProcessorFactory instance()
    {
    	return INSTANCE;
    } 
    
    public IPortalEventProcessor Get(EventType eventType)
    {
    	switch (eventType)
		{		
		case CourseEnrollmentCreate :			
			return new LSEnrollmentProcessor();
		case SMSCourseEnrollmentCreate : 			
			return new SMSEnrollmentProcessor();
		default : return null;		
		}		
    }
    
   public  enum EventType {
    	CourseEnrollmentCreate
    	,SMSCourseEnrollmentCreate
    }
   
   
}
