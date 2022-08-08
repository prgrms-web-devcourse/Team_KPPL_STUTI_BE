package prgrms.project.stuti.domain.feed.service.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;

public record CommentParentContents(
	Long postCommentId,
	Long parentId,
	String profileImageUrl,
	Long memberId,
	String nickname,
	String contents,
	LocalDateTime updatedAt,
	List<PostCommentChildContents> children
) {
	@Builder
	public CommentParentContents {
	}
}
