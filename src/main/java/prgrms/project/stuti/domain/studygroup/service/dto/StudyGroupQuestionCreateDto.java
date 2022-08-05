package prgrms.project.stuti.domain.studygroup.service.dto;

import lombok.Builder;

public record StudyGroupQuestionCreateDto(
	Long memberId,
	Long studyGroupId,
	Long parentId,
	String contents
) {

	@Builder
	public StudyGroupQuestionCreateDto {
	}
}
