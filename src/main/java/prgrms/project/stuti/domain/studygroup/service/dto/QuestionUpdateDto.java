package prgrms.project.stuti.domain.studygroup.service.dto;

public record QuestionUpdateDto(
	Long memberId,
	Long questionId,
	Long studyGroupId,
	String content
) {
}
