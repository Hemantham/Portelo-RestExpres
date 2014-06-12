package com.pearson.portello.domain.event;

import com.pearson.portello.domain.BlogEntry;

public class BlogEntryDeletedEvent
{
	public String blogEntryId;

	public BlogEntryDeletedEvent(BlogEntry deleted)
	{
		this.blogEntryId = deleted.getId();
	}
}
