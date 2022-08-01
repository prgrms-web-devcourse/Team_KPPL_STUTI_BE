package prgrms.project.stuti.domain.studygroup.model;

import lombok.Getter;

@Getter
public enum StudyMemberRole {

	LEADER("리더"), STUDY_MEMBER("멤버"), APPLICANT("신청자");

	private final String value;

	StudyMemberRole(String value) {
		this.value = value;
	}
}
