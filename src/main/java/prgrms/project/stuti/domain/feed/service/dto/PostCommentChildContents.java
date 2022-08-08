package prgrms.project.stuti.domain.feed.service.dto;

import java.time.LocalDateTime;

import lombok.Builder;

public record PostCommentChildContents(
	Long parentId,
	Long commentId,
	String profileImageUrl,
	Long memberId,
	String nickname,
	String contents,
	LocalDateTime createdAt
) {
	@Builder
	public PostCommentChildContents {
	}
}
