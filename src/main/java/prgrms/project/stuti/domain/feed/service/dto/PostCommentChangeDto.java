package prgrms.project.stuti.domain.feed.service.dto;

import lombok.Builder;

public record PostCommentChangeDto(
	Long memberId,
	Long postId,
	Long postCommentId,
	Long parentId,
	String contents
) {
	@Builder
	public PostCommentChangeDto {
	}
}
