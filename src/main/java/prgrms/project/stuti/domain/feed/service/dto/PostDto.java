package prgrms.project.stuti.domain.feed.service.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import prgrms.project.stuti.domain.member.model.Mbti;

public record PostDto(
	Long postId,
	Long memberId,
	String nickname,
	Mbti mbti,
	String profileImageUrl,
	String contents,
	String postImageUrl,
	LocalDateTime createdAt,
	Long totalLikes,
	Long totalComments,
	boolean isliked
) {
	@Builder
	public PostDto {
	}
}
