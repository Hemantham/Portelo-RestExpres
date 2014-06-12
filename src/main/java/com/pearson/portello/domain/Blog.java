package com.pearson.portello.domain;

import com.github.jmkgreen.morphia.annotations.Entity;
import com.pearson.clients.subpub.Message;
import com.strategicgains.syntaxe.annotation.StringValidation;

@Entity("blogs")
public class Blog
extends AbstractLinkableEntity
{
	@StringValidation(name = "Blog Title", required = true)
	private String title;
	private String description;

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	@Override
	public void addContextTags(Message m)
	{
		m.addTag("title", getTitle());
		m.addTag("blogId", getId());
	}
}
