package com.pearson.portello.domain;
import java.util.List;

import com.pearson.model.Card;

public interface IPortalEventProcessor {
	
	public List<Card> ProcessEvent(String payload);	

}
