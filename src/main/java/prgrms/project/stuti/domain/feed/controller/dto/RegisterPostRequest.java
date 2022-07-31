package prgrms.project.stuti.domain.feed.controller.dto;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.springframework.web.multipart.MultipartFile;

public record RegisterPostRequest(
	List<MultipartFile> files,
	@NotNull String content
) {
}
