package prgrms.project.stuti.domain.feed.service.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;

public record PostCommentParent(
	Long postCommentId,
	Long parentId,
	String profileImageUrl,
	Long memberId,
	String nickname,
	String contents,
	LocalDateTime updatedAt,
	List<PostCommentChild> children
) {
	@Builder
	public PostCommentParent {
	}
}
