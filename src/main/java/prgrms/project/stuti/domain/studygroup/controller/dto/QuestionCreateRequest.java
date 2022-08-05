package prgrms.project.stuti.domain.studygroup.controller.dto;

import javax.validation.constraints.NotBlank;

public record QuestionCreateRequest(
	Long parentId,

	@NotBlank
	String content
) {

}
