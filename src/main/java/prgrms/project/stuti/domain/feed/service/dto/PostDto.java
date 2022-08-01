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
	int totalLikes,
	int totalComments,
	boolean isliked
) {
	@Builder
	public PostDto(Long postId, Long memberId, String nickname, Mbti mbti, String profileImageUrl, String contents,
		String postImageUrl, LocalDateTime createdAt, int totalLikes, int totalComments, boolean isliked) {
		this.postId = postId;
		this.memberId = memberId;
		this.nickname = nickname;
		this.mbti = mbti;
		this.profileImageUrl = profileImageUrl;
		this.contents = contents;
		this.postImageUrl = postImageUrl;
		this.createdAt = createdAt;
		this.totalLikes = totalLikes;
		this.totalComments = totalComments;
		this.isliked = isliked;
	}
}
