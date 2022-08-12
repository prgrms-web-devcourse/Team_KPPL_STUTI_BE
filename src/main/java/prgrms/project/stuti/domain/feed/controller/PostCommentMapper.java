package prgrms.project.stuti.domain.feed.controller;

import prgrms.project.stuti.domain.feed.controller.dto.PostCommentRequest;
import prgrms.project.stuti.domain.feed.service.dto.PostCommentUpdateDto;
import prgrms.project.stuti.domain.feed.service.dto.PostCommentCreateDto;

public class PostCommentMapper {

	public static PostCommentCreateDto toPostCommentCreateDto(PostCommentRequest postCommentRequest, Long postId,
		Long memberId) {
		return PostCommentCreateDto.builder()
			.memberId(memberId)
			.postId(postId)
			.parentId(postCommentRequest.parentId())
			.contents(postCommentRequest.contents())
			.build();
	}

	public static PostCommentUpdateDto toPostCommentUpdateDto(PostCommentRequest postCommentRequest, Long postId,
		Long commentId, Long memberId) {
		return PostCommentUpdateDto.builder()
			.memberId(memberId)
			.postId(postId)
			.postCommentId(commentId)
			.parentId(postCommentRequest.parentId())
			.contents(postCommentRequest.contents())
			.build();
	}
}
