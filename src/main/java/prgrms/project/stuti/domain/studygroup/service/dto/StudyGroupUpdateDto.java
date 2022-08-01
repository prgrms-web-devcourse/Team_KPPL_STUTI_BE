package prgrms.project.stuti.domain.studygroup.service.dto;

import org.springframework.web.multipart.MultipartFile;

import lombok.Builder;

public record StudyGroupUpdateDto(
	Long memberId,
	Long studyGroupId,
	String title,
	MultipartFile imageFile,
	String description
) {

	@Builder
	public StudyGroupUpdateDto {
	}
}
