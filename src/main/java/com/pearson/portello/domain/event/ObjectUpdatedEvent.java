package com.pearson.portello.domain.event;

public class ObjectUpdatedEvent
extends StateChangeEvent
{
	public ObjectUpdatedEvent(Object after)
	{
		super(StateChange.UPDATED, after);
	}
}
