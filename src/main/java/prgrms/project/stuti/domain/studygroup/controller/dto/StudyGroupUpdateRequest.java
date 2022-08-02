package prgrms.project.stuti.domain.studygroup.controller.dto;

import javax.validation.constraints.NotBlank;

import org.springframework.web.multipart.MultipartFile;

import prgrms.project.stuti.domain.studygroup.controller.dto.validator.DescriptionLength;
import prgrms.project.stuti.domain.studygroup.controller.dto.validator.TitleLength;

public record StudyGroupUpdateRequest(
	@NotBlank
	@TitleLength
	String title,

	MultipartFile imageFile,

	@NotBlank
	@DescriptionLength
	String description
) {
}
