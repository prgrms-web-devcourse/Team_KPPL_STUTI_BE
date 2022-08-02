package prgrms.project.stuti.domain.studygroup.service.dto;

import lombok.Builder;

public record LeaderResponse(
	Long memberId,
	String profileImageUrl,
	String nickname,
	String field,
	String career,
	String mbti
) {

	@Builder
	public LeaderResponse {
	}
}
