package com.pearson.portello.serialization;

import com.pearson.portello.domain.Blog;
import com.pearson.portello.domain.BlogEntry;
import com.pearson.portello.domain.Comment;
import com.strategicgains.restexpress.serialization.xml.DefaultXmlProcessor;

public class XmlSerializationProcessor
extends DefaultXmlProcessor
{
	public XmlSerializationProcessor()
    {
	    super();
	    alias("blog", Blog.class);
	    alias("blog_entry", BlogEntry.class);
	    alias("comment", Comment.class);
		registerConverter(new XstreamObjectIdConverter());
    }
}
