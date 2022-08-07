package prgrms.project.stuti.domain.studygroup.service.response;

import java.util.List;

import lombok.Builder;

public record StudyGroupMemberManagementResponse(
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
	public StudyGroupMemberManagementResponse {
	}
}
