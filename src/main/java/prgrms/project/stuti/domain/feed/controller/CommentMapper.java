package prgrms.project.stuti.domain.feed.controller;

import prgrms.project.stuti.domain.feed.controller.dto.CommentRequest;
import prgrms.project.stuti.domain.feed.service.dto.CommentUpdateDto;
import prgrms.project.stuti.domain.feed.service.dto.CommentCreateDto;

public class CommentMapper {

	public static CommentCreateDto toCommentCreateDto(CommentRequest commentRequest, Long postId, Long memberId) {
		return CommentCreateDto.builder()
			.memberId(memberId)
			.postId(postId)
			.parentId(commentRequest.parentId())
			.contents(commentRequest.contents())
			.build();
	}

	public static CommentUpdateDto toCommentUpdateDto(CommentRequest commentRequest, Long postId, Long commentId, Long memberId) {
		return CommentUpdateDto.builder()
			.memberId(memberId)
			.postId(postId)
			.postCommentId(commentId)
			.parentId(commentRequest.parentId())
			.contents(commentRequest.contents())
			.build();
	}
}
