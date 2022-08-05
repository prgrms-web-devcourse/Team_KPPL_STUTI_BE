package prgrms.project.stuti.domain.studygroup.service.studymember;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import prgrms.project.stuti.domain.studygroup.service.response.StudyMemberIdResponse;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StudyMemberConverter {

	public static StudyMemberIdResponse toStudyMemberIdResponse(Long studyMemberId) {
		return new StudyMemberIdResponse(studyMemberId);
	}
}
