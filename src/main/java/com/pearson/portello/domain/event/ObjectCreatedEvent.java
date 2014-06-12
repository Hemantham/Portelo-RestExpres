package com.pearson.portello.domain.event;

public class ObjectCreatedEvent
extends StateChangeEvent
{
	public ObjectCreatedEvent(Object data)
	{
		super(StateChange.CREATED, data);
	}
}
