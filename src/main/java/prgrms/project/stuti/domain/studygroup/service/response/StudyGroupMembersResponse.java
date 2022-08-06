package prgrms.project.stuti.domain.studygroup.service.response;

import java.util.List;

import lombok.Builder;
import prgrms.project.stuti.domain.member.model.Mbti;

public record StudyGroupMembersResponse(
	Long studyGroupId,
	String topic,
	String title,
	int numberOfMembers,
	int numberOfRecruits,
	List<StudyGroupMemberResponse> studyMembers,
	int numberOfApplicants,
	List<StudyGroupMemberResponse> studyApplicants
) {

	@Builder
	public StudyGroupMembersResponse {
	}

	public static record StudyGroupMemberResponse(
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
}
