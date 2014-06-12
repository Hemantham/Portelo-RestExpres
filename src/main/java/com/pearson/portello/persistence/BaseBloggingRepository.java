package com.pearson.portello.persistence;

import com.mongodb.Mongo;
import com.pearson.clients.subpub.SubPubMessageBus;
import com.strategicgains.repoexpress.mongodb.AbstractMongodbEntity;
import com.strategicgains.repoexpress.mongodb.MongodbEntityRepository;

public class BaseBloggingRepository<T extends AbstractMongodbEntity>
extends MongodbEntityRepository<T>
{
	public BaseBloggingRepository(Mongo mongo, String databaseName, SubPubMessageBus subpub, Class<T>... types)
	{
		super(mongo, databaseName, types);
		addObserver(new CascadeDeleteRepositoryObserver<T>());

		if (subpub != null)
		{
			addObserver(new StateChangePublishingObserver<T>(subpub));
		}
	}
}
