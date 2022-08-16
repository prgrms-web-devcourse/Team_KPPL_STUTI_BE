package prgrms.project.stuti.domain.post.controller;

import prgrms.project.stuti.domain.post.controller.dto.PostRequest;
import prgrms.project.stuti.domain.post.service.dto.PostChangeDto;
import prgrms.project.stuti.domain.post.service.dto.PostCreateDto;

public class PostMapper {
	public static PostCreateDto toPostCreateDto(PostRequest registerPostRequest, Long memberId) {
		return PostCreateDto.builder()
			.memberId(memberId)
			.contents(registerPostRequest.contents())
			.imageFile(registerPostRequest.postImage())
			.build();
	}

	public static PostChangeDto toPostChangeDto(PostRequest registerPostRequest, Long postId) {
		return PostChangeDto.builder()
			.postId(postId)
			.contents(registerPostRequest.contents())
			.imageFile(registerPostRequest.postImage())
			.build();
	}

}
