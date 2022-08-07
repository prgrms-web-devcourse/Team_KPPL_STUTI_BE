package prgrms.project.stuti.domain.studygroup.service.dto;

import lombok.Builder;

public record StudyGroupQuestionUpdateDto(
	Long memberId,
	Long studyGroupQuestionId,
	Long studyGroupId,
	String contents
) {

	@Builder
	public StudyGroupQuestionUpdateDto {
	}
}
