package com.pearson.portello.domain.event;

public class StateChangeEvent
{
	public Object data;
	public StateChange changeType;
	
	public StateChangeEvent(StateChange changeType, Object data)
	{
		this.data = data;
		this.changeType = changeType;
	}
}
