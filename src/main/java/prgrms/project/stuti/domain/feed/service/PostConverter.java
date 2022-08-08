package prgrms.project.stuti.domain.feed.service;

import java.util.List;

import prgrms.project.stuti.domain.feed.model.Post;
import prgrms.project.stuti.domain.feed.service.dto.PostCreateDto;
import prgrms.project.stuti.domain.feed.service.dto.PostDto;
import prgrms.project.stuti.domain.feed.service.dto.PostIdResponse;
import prgrms.project.stuti.domain.feed.service.dto.PostResponse;
import prgrms.project.stuti.domain.member.model.Member;

public class PostConverter {

	public static Post toPost(PostCreateDto postDto, Member member) {
		return new Post(postDto.contents(), member);
	}

	public static PostIdResponse toPostIdResponse(Long postId) {
		return new PostIdResponse(postId);
	}

	public static PostResponse toPostResponse(List<PostDto> postsDtos, boolean hasNext) {
		return new PostResponse(postsDtos, hasNext);
	}
}
