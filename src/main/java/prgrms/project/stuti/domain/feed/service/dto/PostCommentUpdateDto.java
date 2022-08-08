package prgrms.project.stuti.domain.feed.service.dto;

import lombok.Builder;

public record PostCommentUpdateDto(
	Long memberId,
	Long postId,
	Long postCommentId,
	Long parentId,
	String contents
) {
	@Builder
	public PostCommentUpdateDto {
	}
}
