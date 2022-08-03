package prgrms.project.stuti.domain.feed.controller.dto;

import javax.validation.constraints.NotNull;

import org.springframework.web.multipart.MultipartFile;

public record RegisterPostRequest(
	MultipartFile postImage,
	@NotNull String contents
) {
}
