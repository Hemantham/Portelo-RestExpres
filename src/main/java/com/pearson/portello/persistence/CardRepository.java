package com.pearson.portello.persistence;

import com.mongodb.Mongo;
import com.pearson.clients.subpub.SubPubMessageBus;
import com.pearson.portello.domain.Blog;
import com.pearson.portello.domain.Card;
import com.strategicgains.repoexpress.mongodb.MongodbEntityRepository;
import com.strategicgains.repoexpress.mongodb.MongodbRepository;

public class CardRepository
extends MongodbEntityRepository<Card>
{
	public CardRepository(Mongo mongo, String databaseName)
	{
		super(mongo, databaseName, Card.class);
	}
}
