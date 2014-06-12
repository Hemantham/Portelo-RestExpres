package com.pearson.portello;

import org.jboss.netty.handler.codec.http.HttpMethod;

import com.pearson.portello.config.Configuration;
import com.strategicgains.restexpress.RestExpress;

public abstract class Routes
{
	public static void define(Configuration config, RestExpress server)
	{
		server.uri("/blogs.{format}", config.getBlogController())
			.action("readAll", HttpMethod.GET)
			.method(HttpMethod.POST)
			.name(Constants.Routes.BLOGS);

		server.uri("/blogs/{blogId}.{format}", config.getBlogController())
			.method(HttpMethod.GET, HttpMethod.PUT, HttpMethod.DELETE)
			.name(Constants.Routes.BLOG);

		server.uri("/blogs/{blogId}/entries.{format}", config.getBlogEntryController())
			.action("readAll", HttpMethod.GET)
			.method(HttpMethod.POST)
			.name(Constants.Routes.BLOG_ENTRIES);

		server.uri("/blogs/{blogId}/entries/{entryId}.{format}", config.getBlogEntryController())
			.method(HttpMethod.GET, HttpMethod.PUT, HttpMethod.DELETE)
			.name(Constants.Routes.BLOG_ENTRY);

		server.uri("/blogs/{blogId}/entries/{entryId}/comments.{format}", config.getCommentController())
			.action("readAll", HttpMethod.GET)
			.method(HttpMethod.POST)
			.name(Constants.Routes.COMMENTS);

		server.uri("/blogs/{blogId}/entries/{entryId}/comments/{commentId}.{format}", config.getCommentController())
			.method(HttpMethod.GET, HttpMethod.PUT, HttpMethod.DELETE)
			.name(Constants.Routes.COMMENT);
		
		server.uri("/CourseEnrollment", config.getEndpointController())
		//.method(HttpMethod.GET, HttpMethod.PUT, HttpMethod.DELETE)
		.action( "CourseEnrollment", HttpMethod.POST)
		.name("endpoint");
		
		server.uri("/Authenticate", config.getAuthenticateController())
		//.method(HttpMethod.GET, HttpMethod.PUT, HttpMethod.DELETE)
		.action( "Authenticate", HttpMethod.POST)
		.name("Authenticate");
		
		server.uri("/user/{userid}/cards", config.getCardController())
		.action("rootFilter", HttpMethod.GET)
		.method(HttpMethod.GET)
		.name(Constants.Routes.SAMPLE_COLLECTION);
	
		server.uri("/user/{userid}/cards", config.getCardController())
		.method(HttpMethod.POST)
		.name(Constants.Routes.SAMPLE_COLLECTION);
	
			
		//List all cards for a user in a card
		server.uri("/user/{userid}/cards/{cardid}/children", config.getCardController())
		.action("chiledFilter", HttpMethod.GET)
		.method(HttpMethod.POST,HttpMethod.GET,HttpMethod.DELETE)
		.name(Constants.Routes.SAMPLE_COLLECTION);

		
		server.uri("/user/cards/{sampleId}.{format}", config.getCardController())
		.method(HttpMethod.GET, HttpMethod.PUT, HttpMethod.DELETE)
		.name(Constants.Routes.SINGLE_SAMPLE);
			
		server.uri("/user/{userid}", config.getCardController())
			.action("readAll", HttpMethod.GET)
			.method(HttpMethod.GET)
			.name(Constants.Routes.SAMPLE_COLLECTION);
		
		server.uri("/status", config.getCardController())
		.action("status", HttpMethod.GET)
		.name("status");
		
		server.uri("/user/{userid}/course/{courseid}/link", config.getAuthenticateController())
		.action("LinkUri", HttpMethod.GET)
		.name("LinkUri");
		
				
		
	}
}
