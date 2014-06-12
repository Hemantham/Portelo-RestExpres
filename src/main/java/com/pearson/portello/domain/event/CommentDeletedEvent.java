package com.pearson.portello.domain.event;

import com.pearson.portello.domain.Comment;

public class CommentDeletedEvent
{
	public String commentId;

	public CommentDeletedEvent(Comment deleted)
	{
		this.commentId = deleted.getId();
	}
}
