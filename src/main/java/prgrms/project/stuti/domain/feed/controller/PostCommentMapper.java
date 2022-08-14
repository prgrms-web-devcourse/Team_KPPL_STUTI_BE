package prgrms.project.stuti.domain.feed.controller;

import prgrms.project.stuti.domain.feed.controller.dto.PostCommentRequest;
import prgrms.project.stuti.domain.feed.service.dto.PostCommentChangeDto;
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

	public static PostCommentChangeDto toPostCommentChangeDto(PostCommentRequest postCommentRequest, Long postId,
		Long commentId, Long memberId) {
		return PostCommentChangeDto.builder()
			.memberId(memberId)
			.postId(postId)
			.postCommentId(commentId)
			.parentId(postCommentRequest.parentId())
			.contents(postCommentRequest.contents())
			.build();
	}
}
