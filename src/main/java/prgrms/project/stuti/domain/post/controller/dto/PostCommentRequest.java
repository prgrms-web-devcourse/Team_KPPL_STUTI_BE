package prgrms.project.stuti.domain.post.controller.dto;

import javax.validation.constraints.NotBlank;

public record PostCommentRequest(
	Long parentId,

	@NotBlank
	String contents
) {
}
