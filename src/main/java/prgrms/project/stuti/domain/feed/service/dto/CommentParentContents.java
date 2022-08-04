package prgrms.project.stuti.domain.feed.service.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;

public record CommentParentContents(
	Long commentId,
	Long parentId,
	String profileImageUrl,
	Long memberId,
	String nickname,
	String contents,
	LocalDateTime createdAt,
	List<CommentChildContents> children
) {
	@Builder
	public CommentParentContents {
	}
}
