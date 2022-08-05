package prgrms.project.stuti.domain.feed.service.dto;

import java.time.LocalDateTime;

import lombok.Builder;

public record CommentResponse(
	Long postCommentId,
	Long parentId,
	String profileImageUrl,
	Long memberId,
	String nickname,
	String contents,
	LocalDateTime updatedAt
) {
	@Builder
	public CommentResponse {
	}
}
