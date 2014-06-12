package com.pearson.portello.domain;

import com.pearson.clients.subpub.Message;

/**
 * Domain objects that are marked as Taggable are able to add context tags to a SubPub message before state changes
 * are published.
 * 
 * @since Feb 7, 2013
 * @see MessageFactory
 */
public interface Taggable
{
	public void addContextTags(Message message);
}
