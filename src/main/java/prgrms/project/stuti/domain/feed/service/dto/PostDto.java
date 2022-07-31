package prgrms.project.stuti.domain.feed.service.dto;

import org.springframework.web.multipart.MultipartFile;

import lombok.Builder;

public record PostDto(
	Long memberId,
	String contents,
	MultipartFile imageFile
) {

	@Builder
	public PostDto(Long memberId, String contents, MultipartFile imageFile) {
		this.memberId = memberId;
		this.contents = contents;
		this.imageFile = imageFile;
	}
}
