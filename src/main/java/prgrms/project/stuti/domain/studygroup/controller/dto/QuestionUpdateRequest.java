package prgrms.project.stuti.domain.studygroup.controller.dto;

import javax.validation.constraints.NotBlank;

public record QuestionUpdateRequest(
	@NotBlank
	String content
) {
}
