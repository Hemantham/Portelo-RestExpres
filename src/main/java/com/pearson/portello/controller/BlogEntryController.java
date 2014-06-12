package com.pearson.portello.controller;

import java.util.List;

import org.jboss.netty.handler.codec.http.HttpMethod;

import com.pearson.portello.Constants;
import com.pearson.portello.domain.BlogEntry;
import com.pearson.portello.persistence.BlogEntryRepository;
import com.strategicgains.hyperexpress.RelTypes;
import com.strategicgains.hyperexpress.domain.Link;
import com.strategicgains.hyperexpress.domain.LinkableCollection;
import com.strategicgains.hyperexpress.util.LinkUtils;
import com.strategicgains.restexpress.Request;
import com.strategicgains.restexpress.Response;
import com.strategicgains.restexpress.common.query.FilterOperator;
import com.strategicgains.restexpress.common.query.QueryFilter;
import com.strategicgains.restexpress.common.query.QueryOrder;
import com.strategicgains.restexpress.common.query.QueryRange;
import com.strategicgains.restexpress.exception.BadRequestException;
import com.strategicgains.restexpress.query.QueryFilters;
import com.strategicgains.restexpress.query.QueryOrders;
import com.strategicgains.restexpress.query.QueryRanges;
import com.strategicgains.syntaxe.ValidationEngine;

public class BlogEntryController
{
	private BlogEntryRepository blogEntries;
	
	public BlogEntryController(BlogEntryRepository blogEntryRepository)
	{
		super();
		this.blogEntries = blogEntryRepository;
	}

	public String create(Request request, Response response)
	{
		BlogEntry blogEntry = request.getBodyAs(BlogEntry.class, "BlogEntry details not provided");
		String blogId = request.getUrlDecodedHeader(Constants.Url.BLOG_ID, "No Blog ID provided");
		blogEntry.setBlogId(blogId);
		ValidationEngine.validateAndThrow(blogEntry);
		BlogEntry saved = blogEntries.create(blogEntry);

		// Construct the response for create...
		response.setResponseCreated();

		// Include the Location header...
		String locationUrl = request.getNamedUrl(HttpMethod.GET, Constants.Routes.BLOG_ENTRY);
		response.addLocationHeader(LinkUtils.formatUrl(locationUrl,
				Constants.Url.BLOG_ENTRY_ID, saved.getId(),
				Constants.Url.BLOG_ID, blogId));

		// Return the newly-created ID...
		return saved.getId();
	}

	public BlogEntry read(Request request, Response response)
	{
		String id = request.getUrlDecodedHeader(Constants.Url.BLOG_ENTRY_ID, "No BlogEntry ID supplied");
		BlogEntry result = blogEntries.read(id);

		// Add 'self' link
		String selfUrlPattern = request.getNamedUrl(HttpMethod.GET, Constants.Routes.BLOG_ENTRY);
		String selfUrl = LinkUtils.formatUrl(selfUrlPattern,
			Constants.Url.BLOG_ID, result.getBlogId(),
			Constants.Url.BLOG_ENTRY_ID, result.getId());
		result.addLink(new Link(RelTypes.SELF, selfUrl));

		// Add 'comments' link
		String commentsUrlPattern = request.getNamedUrl(HttpMethod.GET, Constants.Routes.COMMENTS);
		String commentsUrl = LinkUtils.formatUrl(commentsUrlPattern,
			Constants.Url.BLOG_ID, result.getBlogId(),
			Constants.Url.BLOG_ENTRY_ID, result.getId());
		result.addLink(new Link("http://www.pearson.com/pts/2012/blogging/comments", commentsUrl, "This Blog-Entry's Comments"));

		// Add 'up' (or 'parent') link
		String parentUrlPattern = request.getNamedUrl(HttpMethod.GET, Constants.Routes.BLOG);
		String parentUrl = LinkUtils.formatUrl(parentUrlPattern, Constants.Url.BLOG_ID, result.getBlogId());
		result.addLink(new Link(RelTypes.UP, parentUrl, "The Parent Blog (of this entry)"));

		return result;
	}

	public LinkableCollection<BlogEntry> readAll(Request request, Response response)
	{
		String blogId = request.getUrlDecodedHeader(Constants.Url.BLOG_ID, "Blog ID not provided");
		QueryFilter filter = QueryFilters.parseFrom(request);
		QueryOrder order = QueryOrders.parseFrom(request);
		QueryRange range = QueryRanges.parseFrom(request, 20);

		filter.addCriteria("blogId", FilterOperator.EQUALS, blogId);
		List<BlogEntry> results = blogEntries.readAll(filter, range, order);
		response.setCollectionResponse(range, results.size(), blogEntries.count(filter));

		// Add 'self' and 'comments' links
		String selfUrlPattern = request.getNamedUrl(HttpMethod.GET, Constants.Routes.BLOG_ENTRY);
		String commentsUrlPattern = request.getNamedUrl(HttpMethod.GET, Constants.Routes.COMMENTS);

		for (BlogEntry entry : results)
		{
			String selfUrl = LinkUtils.formatUrl(selfUrlPattern,
				Constants.Url.BLOG_ID, entry.getBlogId(),
				Constants.Url.BLOG_ENTRY_ID, entry.getId());
			entry.addLink(new Link(RelTypes.SELF, selfUrl));

			String commentsUrl = LinkUtils.formatUrl(commentsUrlPattern,
				Constants.Url.BLOG_ID, entry.getBlogId(),
				Constants.Url.BLOG_ENTRY_ID, entry.getId());
			entry.addLink(new Link("http://www.pearson.com/pts/2012/blogging/comments", commentsUrl, "This Blog-Entry's Comments"));
		}

		// Add 'parent' link to the collection
		LinkableCollection<BlogEntry> wrapper = new LinkableCollection<BlogEntry>(results);
		String parentUrlPattern = request.getNamedUrl(HttpMethod.GET, Constants.Routes.BLOG);
		String parentUrl = LinkUtils.formatUrl(parentUrlPattern, Constants.Url.BLOG_ID, blogId);
		wrapper.addLink(new Link(RelTypes.UP, parentUrl, "The Parent Blog (of these entries)"));
		return wrapper;
	}

	public void update(Request request, Response response)
	{
		String id = request.getUrlDecodedHeader(Constants.Url.BLOG_ENTRY_ID);
		BlogEntry blogEntry = request.getBodyAs(BlogEntry.class, "BlogEntry details not provided");
		
		if (!id.equals(blogEntry.getId()))
		{
			throw new BadRequestException("ID in URL and ID in BlogEntry must match");
		}
		
		ValidationEngine.validateAndThrow(blogEntry);
		blogEntries.update(blogEntry);
		response.setResponseNoContent();
	}

	public void delete(Request request, Response response)
	{
		String id = request.getUrlDecodedHeader(Constants.Url.BLOG_ENTRY_ID, "No BlogEntry ID supplied");
		blogEntries.delete(id);
		response.setResponseNoContent();
	}
}
