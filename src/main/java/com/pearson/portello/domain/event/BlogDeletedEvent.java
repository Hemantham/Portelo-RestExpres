package com.pearson.portello.domain.event;

import com.pearson.portello.domain.Blog;

public class BlogDeletedEvent
{
	public String blogId;

	public BlogDeletedEvent(Blog deleted)
	{
		this.blogId = deleted.getId();
	}
}
