package com.pearson.portello.controller;

import java.util.List;

import org.jboss.netty.handler.codec.http.HttpMethod;

import com.pearson.portello.Constants;
import com.pearson.portello.domain.Blog;
import com.pearson.portello.persistence.BlogRepository;
import com.strategicgains.hyperexpress.RelTypes;
import com.strategicgains.hyperexpress.domain.Link;
import com.strategicgains.hyperexpress.util.LinkUtils;
import com.strategicgains.restexpress.Request;
import com.strategicgains.restexpress.Response;
import com.strategicgains.restexpress.common.query.QueryFilter;
import com.strategicgains.restexpress.common.query.QueryOrder;
import com.strategicgains.restexpress.common.query.QueryRange;
import com.strategicgains.restexpress.exception.BadRequestException;
import com.strategicgains.restexpress.query.QueryFilters;
import com.strategicgains.restexpress.query.QueryOrders;
import com.strategicgains.restexpress.query.QueryRanges;
import com.strategicgains.syntaxe.ValidationEngine;

public class BlogController
{
	private BlogRepository blogs;
	
	public BlogController(BlogRepository blogRepository)
	{
		super();
		this.blogs = blogRepository;
	}

	public String create(Request request, Response response)
	{
		Blog blog = request.getBodyAs(Blog.class, "Blog details not provided");
		ValidationEngine.validateAndThrow(blog);
		Blog saved = blogs.create(blog);

		// Construct the response for create...
		response.setResponseCreated();

		// Include the Location header...
		String locationUrl = request.getNamedUrl(HttpMethod.GET, Constants.Routes.BLOG);
		response.addLocationHeader(LinkUtils.formatUrl(locationUrl, Constants.Url.BLOG_ID, saved.getId()));

		// Return the newly-created ID...
		return saved.getId();
	}

	public Blog read(Request request, Response response)
	{
		String id = request.getUrlDecodedHeader(Constants.Url.BLOG_ID, "No Blog ID supplied");
		Blog result = blogs.read(id);

		// Add 'self' link
		String selfUrlPattern = request.getNamedUrl(HttpMethod.GET, Constants.Routes.BLOG);
		String selfUrl = LinkUtils.formatUrl(selfUrlPattern, Constants.Url.BLOG_ID, result.getId());
		result.addLink(new Link(RelTypes.SELF, selfUrl));

		// Add 'entries' link
		String entriesUrlPattern = request.getNamedUrl(HttpMethod.GET, Constants.Routes.BLOG_ENTRIES);
		String entriesUrl = LinkUtils.formatUrl(entriesUrlPattern, Constants.Url.BLOG_ID, result.getId());
		result.addLink(new Link("http://www.pearson.com/pts/2012/blogging/entries", entriesUrl, "This Blog's Entries"));
		return result;
	}

	public List<Blog> readAll(Request request, Response response)
	{
		QueryFilter filter = QueryFilters.parseFrom(request);
		QueryOrder order = QueryOrders.parseFrom(request);
		QueryRange range = QueryRanges.parseFrom(request, 20);
		List<Blog> results = blogs.readAll(filter, range, order);
		response.setCollectionResponse(range, results.size(), blogs.count(filter));
		
		// Add 'self' links
		String urlPattern = request.getNamedUrl(HttpMethod.GET, Constants.Routes.BLOG);
		String entriesUrlPattern = request.getNamedUrl(HttpMethod.GET, Constants.Routes.BLOG_ENTRIES);
		
		for (Blog blog : results)
		{
			String selfUrl = LinkUtils.formatUrl(urlPattern, Constants.Url.BLOG_ID, blog.getId());
			blog.addLink(new Link(RelTypes.SELF, selfUrl));

			// Add 'entries' link
			String entriesUrl = LinkUtils.formatUrl(entriesUrlPattern, Constants.Url.BLOG_ID, blog.getId());
			blog.addLink(new Link("http://www.pearson.com/pts/2012/blogging/entries", entriesUrl, "This Blog's Entries"));
		}

		return results;
	}

	public void update(Request request, Response response)
	{
		String id = request.getUrlDecodedHeader(Constants.Url.BLOG_ID);
		Blog blog = request.getBodyAs(Blog.class, "Blog details not provided");
		
		if (!id.equals(blog.getId()))
		{
			throw new BadRequestException("ID in URL and ID in Blog must match");
		}
		
		ValidationEngine.validateAndThrow(blog);
		blogs.update(blog);
		response.setResponseNoContent();
	}

	public void delete(Request request, Response response)
	{
		String id = request.getUrlDecodedHeader(Constants.Url.BLOG_ID, "No Blog ID supplied");
		blogs.delete(id);
		response.setResponseNoContent();
	}
}
