package prgrms.project.stuti.domain.feed.controller.dto;

import javax.validation.constraints.NotBlank;

public record CommentRequest(
	Long parentId,

	@NotBlank
	String contents
) {
}
