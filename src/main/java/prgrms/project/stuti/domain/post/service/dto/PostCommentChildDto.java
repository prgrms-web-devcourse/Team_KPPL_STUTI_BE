package prgrms.project.stuti.domain.post.service.dto;

import java.time.LocalDateTime;

import lombok.Builder;

public record PostCommentChildDto(
	Long parentId,
	Long postCommentId,
	String profileImageUrl,
	Long memberId,
	String nickname,
	String contents,
	LocalDateTime updatedAt
) {
	@Builder
	public PostCommentChildDto {
	}
}
