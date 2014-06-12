package com.pearson.portello.persistence;

import com.pearson.portello.domain.Blog;
import com.pearson.portello.domain.BlogEntry;
import com.pearson.portello.domain.event.BlogDeletedEvent;
import com.pearson.portello.domain.event.BlogEntryDeletedEvent;
import com.strategicgains.eventing.DomainEvents;
import com.strategicgains.repoexpress.domain.TimestampedIdentifiable;
import com.strategicgains.repoexpress.event.AbstractRepositoryObserver;

public class CascadeDeleteRepositoryObserver<T extends TimestampedIdentifiable>
extends AbstractRepositoryObserver<T>
{
	public CascadeDeleteRepositoryObserver()
	{
		super();
	}

	@Override
	public void beforeDelete(T object)
	{
		if (Blog.class.isAssignableFrom(object.getClass()))
		{
			DomainEvents.publish(new BlogDeletedEvent((Blog) object));
		}
		else if (BlogEntry.class.isAssignableFrom(object.getClass()))
		{
			DomainEvents.publish(new BlogEntryDeletedEvent((BlogEntry) object));
		}
	}
}