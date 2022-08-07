package prgrms.project.stuti.domain.feed.service;

import prgrms.project.stuti.domain.feed.model.Comment;
import prgrms.project.stuti.domain.feed.model.Feed;
import prgrms.project.stuti.domain.feed.service.dto.CommentResponse;

public class CommentConverter {

	public static Comment toComment(String contents, Feed feed, Comment parentComment) {
		return new Comment(contents, parentComment, feed.getMember(), feed);
	}

	public static CommentResponse toCommentResponse(Comment savedComment) {
		Long parentId = null;
		if (savedComment.getParent() != null) {
			parentId = savedComment.getParent().getId();
		}
		return CommentResponse.builder()
			.postCommentId(savedComment.getId())
			.parentId(parentId)
			.profileImageUrl(savedComment.getMember().getProfileImageUrl())
			.memberId(savedComment.getMember().getId())
			.nickname(savedComment.getMember().getNickName())
			.contents(savedComment.getContent())
			.updatedAt(savedComment.getUpdatedAt())
			.build();
	}
}
