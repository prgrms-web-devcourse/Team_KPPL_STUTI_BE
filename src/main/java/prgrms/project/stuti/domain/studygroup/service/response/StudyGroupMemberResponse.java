package prgrms.project.stuti.domain.studygroup.service.response;

import lombok.Builder;
import prgrms.project.stuti.domain.member.model.Mbti;

public record StudyGroupMemberResponse(
	Long studyGroupMemberId,
	String profileImageUrl,
	String nickname,
	String field,
	String career,
	Mbti mbti,
	String studyGroupMemberRole
) {

	@Builder
	public StudyGroupMemberResponse {
	}
}
