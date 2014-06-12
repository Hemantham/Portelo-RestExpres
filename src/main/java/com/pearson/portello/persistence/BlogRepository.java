package com.pearson.portello.persistence;

import com.mongodb.Mongo;
import com.pearson.clients.subpub.SubPubMessageBus;
import com.pearson.portello.domain.Blog;

public class BlogRepository
extends BaseBloggingRepository<Blog>
{
	public BlogRepository(Mongo mongo, String databaseName, SubPubMessageBus subpub)
	{
		super(mongo, databaseName, subpub, Blog.class);
	}
}
