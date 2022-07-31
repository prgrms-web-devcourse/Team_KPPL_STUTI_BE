package prgrms.project.stuti.domain.feed.controller;

import prgrms.project.stuti.domain.feed.controller.dto.RegisterPostRequest;
import prgrms.project.stuti.domain.feed.service.dto.PostDto;

public class FeedMapper {
	public static PostDto toPostDto(RegisterPostRequest registerPostRequest, Long memberId) {
		return PostDto.builder()
			.memberId(memberId)
			.contents(registerPostRequest.content())
			.imageFile(registerPostRequest.imageFile())
			.build();
	}
}
