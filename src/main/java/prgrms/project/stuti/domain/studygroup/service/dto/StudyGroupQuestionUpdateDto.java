package prgrms.project.stuti.domain.studygroup.service.dto;

public record StudyGroupQuestionUpdateDto(
	Long memberId,
	Long studyGroupQuestionId,
	Long studyGroupId,
	String contents
) {
}
