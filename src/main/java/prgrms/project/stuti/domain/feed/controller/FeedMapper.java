package prgrms.project.stuti.domain.feed.controller;

import prgrms.project.stuti.domain.feed.controller.dto.RegisterPostRequest;
import prgrms.project.stuti.domain.feed.service.dto.PostCreateDto;

public class FeedMapper {
	public static PostCreateDto toPostDto(RegisterPostRequest registerPostRequest, Long memberId) {
		return PostCreateDto.builder()
			.memberId(memberId)
			.contents(registerPostRequest.content())
			.imageFile(registerPostRequest.imageFile())
			.build();
	}
}
