package com.pearson.portello.persistence;

import com.github.jmkgreen.morphia.query.Query;
import com.mongodb.Mongo;
import com.pearson.clients.subpub.SubPubMessageBus;
import com.pearson.portello.domain.Comment;

public class CommentRepository
extends BaseBloggingRepository<Comment>
{
	public CommentRepository(Mongo mongo, String databaseName, SubPubMessageBus subpub)
	{
		super(mongo, databaseName, subpub, Comment.class);
	}

	public void deleteByBlogEntryId(String blogEntryId)
	{
		Query<Comment> comments = getDataStore().createQuery(Comment.class).field("blogEntryId").equal(blogEntryId);
		getDataStore().delete(comments);
	}

	public void deleteByBlogEntryIds(Iterable<String> blogEntryIds)
	{
		if (blogEntryIds.iterator().hasNext())
		{
			Query<Comment> comments = getDataStore().createQuery(Comment.class).field("blogEntryId").in(blogEntryIds);
			getDataStore().delete(comments);
		}
	}
}
