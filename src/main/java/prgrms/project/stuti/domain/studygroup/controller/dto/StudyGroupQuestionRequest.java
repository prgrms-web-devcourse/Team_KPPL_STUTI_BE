package prgrms.project.stuti.domain.studygroup.controller.dto;

import javax.validation.constraints.NotBlank;

public record StudyGroupQuestionRequest() {

	public static record CreateRequest(
		Long parentId,

		@NotBlank
		String contents
	) {
	}

	public static record UpdateRequest(
		@NotBlank
		String contents
	) {
	}
}
