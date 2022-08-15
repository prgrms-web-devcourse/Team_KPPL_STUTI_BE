package prgrms.project.stuti.domain.post.service.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;

public record PostCommentParentDto(
	Long postCommentId,
	Long parentId,
	String profileImageUrl,
	Long memberId,
	String nickname,
	String contents,
	LocalDateTime updatedAt,
	List<PostCommentChildDto> children
) {
	@Builder
	public PostCommentParentDto {
	}
}
