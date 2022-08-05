package prgrms.project.stuti.domain.studygroup.service.response;

import lombok.Builder;
import prgrms.project.stuti.domain.member.model.Mbti;

public record StudyMemberResponse(
	Long memberId,
	String profileImageUrl,
	String nickname,
	String field,
	String career,
	Mbti mbti
) {

	@Builder
	public StudyMemberResponse {
	}
}
