package prgrms.project.stuti.domain.feed.service;

import prgrms.project.stuti.domain.feed.model.Comment;
import prgrms.project.stuti.domain.feed.model.Feed;
import prgrms.project.stuti.domain.feed.service.dto.CommentIdResponse;

public class CommentConverter {

	public static Comment toComment(String contents, Feed feed, Comment parentComment) {
		return new Comment(contents, parentComment, feed.getMember(), feed);
	}

	public static CommentIdResponse toCommentIdResponse(Long commentId) {
		return new CommentIdResponse(commentId);
	}
}
