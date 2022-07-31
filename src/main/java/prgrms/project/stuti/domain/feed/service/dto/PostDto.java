package prgrms.project.stuti.domain.feed.service.dto;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lombok.Builder;

public record PostDto(
	Long memberId,
	String contents,
	List<MultipartFile> files
) {

	@Builder
	public PostDto(Long memberId, String contents, List<MultipartFile> files) {
		this.memberId = memberId;
		this.contents = contents;
		this.files = files;
	}
}
