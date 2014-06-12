package com.pearson.portello.persistence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pearson.clients.subpub.Message;
import com.pearson.clients.subpub.PublishResponse;
import com.pearson.clients.subpub.SubPubMessageBus;
import com.pearson.portello.domain.MessageFactory;
import com.strategicgains.repoexpress.domain.TimestampedIdentifiable;
import com.strategicgains.repoexpress.event.AbstractRepositoryObserver;
import com.strategicgains.restexpress.exception.ServiceException;

public class StateChangePublishingObserver<T extends TimestampedIdentifiable>
extends AbstractRepositoryObserver<T>
{
	private static final Logger LOG = LoggerFactory.getLogger(StateChangePublishingObserver.class);
	private static final String MESSAGE_TYPE_PREFIX = "Reference.";

	private SubPubMessageBus subpub;

	public StateChangePublishingObserver(SubPubMessageBus subPubMessageBus)
	{
		super();
		this.subpub = subPubMessageBus;
	}

	@Override
	public void afterCreate(T object)
	{
		if (subpub == null) return;

		Message m = MessageFactory.instance().toMessage(object);
		m.setMessageType(MESSAGE_TYPE_PREFIX + getMessageName(object) + ".Created");
		publish(m);
	}

	@Override
	public void beforeDelete(T object)
	{
		if (subpub == null) return;

		Message m = MessageFactory.instance().toMessage(object);
		m.setMessageType(MESSAGE_TYPE_PREFIX + getMessageName(object) + ".Deleted");
		publish(m);
	}

	@Override
	public void afterUpdate(T object)
	{
		if (subpub == null) return;

		Message m = MessageFactory.instance().toMessage(object);
		m.setMessageType(MESSAGE_TYPE_PREFIX + getMessageName(object) + ".Updated");
		publish(m);
	}


	//SECTION: UTILITY - PRIVATE

	private String getMessageName(T object)
    {
	    return object.getClass().getSimpleName();
    }

	private void publish(Message m)
    {
	    PublishResponse r = subpub.publish(m);
		
		if (r.getException() != null)
		{
			throw new ServiceException(r.getException());
		}
		else
		{
			LOG.info("MessageId: " + r.getMessageId());
		}
    }
}