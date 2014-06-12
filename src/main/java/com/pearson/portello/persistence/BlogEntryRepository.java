package com.pearson.portello.persistence;

import com.github.jmkgreen.morphia.query.Query;
import com.mongodb.Mongo;
import com.pearson.clients.subpub.SubPubMessageBus;
import com.pearson.portello.domain.BlogEntry;
import com.strategicgains.repoexpress.util.IdentifiableIterable;

public class BlogEntryRepository
extends BaseBloggingRepository<BlogEntry>
{
	public BlogEntryRepository(Mongo mongo, String databaseName, SubPubMessageBus subpub)
	{
		super(mongo, databaseName, subpub, BlogEntry.class);
	}

	public Iterable<String> findIdsByBlogId(String blogId)
	{
		Query<BlogEntry> blogEntries = getDataStore().createQuery(BlogEntry.class).field("blogId").equal(blogId).retrievedFields(true, "_id");
		return new IdentifiableIterable(blogEntries.fetch());		
	}

	public void deleteByBlogId(String blogId)
	{
		Query<BlogEntry> blogEntries = getDataStore().createQuery(BlogEntry.class).field("blogId").equal(blogId);
		getDataStore().delete(blogEntries);
	}
}
