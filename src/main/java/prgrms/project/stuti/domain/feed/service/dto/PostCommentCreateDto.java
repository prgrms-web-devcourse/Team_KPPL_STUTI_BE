package prgrms.project.stuti.domain.feed.service.dto;

import lombok.Builder;

public record PostCommentCreateDto(
	Long memberId,
	Long postId,
	Long parentId,
	String contents
) {
	@Builder
	public PostCommentCreateDto {
	}
}
