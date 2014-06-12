package com.pearson.portello.domain;

import java.util.List;

import com.pearson.clients.subpub.Message;
import com.pearson.model.Link;


public class Card extends AbstractLinkableEntity{

	public Card() {
		// TODO Auto-generated constructor stub
	}
	//public String cardId; 
	public String linkUri ;
	public String referenceId ;
	public String cardType ;
	public int cardLevel ;
    public String previewImageUrl;
	public String title ;
	public Link[] externalLinks;	
	public int[] children; 
	public String description;
	public String userId;
	public String parentId;	
	public String imageUrl; 

	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	public String getLinkUri() {
		return linkUri;
	}
	public void setLinkUri(String linkUri) {
		this.linkUri = linkUri;
	}
	public String getReferenceId() {
		return referenceId;
	}
	public void setReferenceId(String referenceId) {
		this.referenceId = referenceId;
	}
	public String getCardType() {
		return cardType;
	}
	public void setCardType(String cardType) {
		this.cardType = cardType;
	}
	public int getCardLevel() {
		return cardLevel;
	}
	public void setCardLevel(int cardLevel) {
		this.cardLevel = cardLevel;
	}
	public String getPreviewImageUrl() {
		return previewImageUrl;
	}
	public void setPreviewImageUrl(String previewImageUrl) {
		this.previewImageUrl = previewImageUrl;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Link[] getExternalLinks() {
		return externalLinks;
	}
	public void setExternalLinks(Link[] links) {
		this.externalLinks = links;
	}
	public int[] getChildren() {
		return children;
	}
	public void setChildren(int[] children) {
		this.children = children;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	
	@Override
	public void addContextTags(Message message) {
		// TODO Auto-generated method stub
		
	}
	
	
}


