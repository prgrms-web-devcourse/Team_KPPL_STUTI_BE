package prgrms.project.stuti.domain.studygroup.service.dto;

import lombok.Builder;

public record QuestionCreateDto(
	Long memberId,
	Long studyGroupId,
	Long parentId,
	String content
) {

	@Builder
	public QuestionCreateDto {
	}
}
