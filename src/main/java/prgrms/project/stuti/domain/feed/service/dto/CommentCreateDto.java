package prgrms.project.stuti.domain.feed.service.dto;

import lombok.Builder;

public record CommentCreateDto(
	Long memberId,
	Long postId,
	Long parentId,
	String contents
) {
	@Builder
	public CommentCreateDto{
	}
}
