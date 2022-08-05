package prgrms.project.stuti.domain.studygroup.model;

import lombok.Getter;

@Getter
public enum StudyGroupMemberRole {

	STUDY_LEADER("리더"), STUDY_MEMBER("멤버"), STUDY_APPLICANT("신청자");

	private final String value;

	StudyGroupMemberRole(String value) {
		this.value = value;
	}
}
