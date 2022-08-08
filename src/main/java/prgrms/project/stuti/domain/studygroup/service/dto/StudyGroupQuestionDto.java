package prgrms.project.stuti.domain.studygroup.service.dto;

import lombok.Builder;

public record StudyGroupQuestionDto() {

	public static record CreateDto(
		Long memberId,
		Long studyGroupId,
		Long parentId,
		String contents
	) {

		@Builder
		public CreateDto {
		}
	}

	public static record PageDto(
		Long studyGroupId,
		Long size,
		Long lastStudyGroupQuestionId
	) {

	}

	public static record UpdateDto(
		Long memberId,
		Long studyGroupQuestionId,
		Long studyGroupId,
		String contents
	) {

		@Builder
		public UpdateDto {
		}
	}

	public static record DeleteDto(
		Long memberId,
		Long studyGroupId,
		Long studyGroupQuestionId
	) {

	}
}
