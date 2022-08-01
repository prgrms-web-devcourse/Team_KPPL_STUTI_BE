package prgrms.project.stuti.domain.feed.service;

import java.util.List;

import prgrms.project.stuti.domain.feed.model.Feed;
import prgrms.project.stuti.domain.feed.service.dto.PostCreateDto;
import prgrms.project.stuti.domain.feed.service.dto.PostDto;
import prgrms.project.stuti.domain.feed.service.dto.PostIdResponse;
import prgrms.project.stuti.domain.feed.service.dto.FeedResponse;
import prgrms.project.stuti.domain.member.model.Member;

public class FeedConverter {

	public static Feed toPost(PostCreateDto postDto, Member member) {
		return new Feed(postDto.contents(), member);
	}

	public static PostIdResponse toPostIdResponse(Long postId) {
		return new PostIdResponse(postId);
	}

	public static FeedResponse toFeedResponse(List<PostDto> postsDtos, boolean hasNext) {
		return new FeedResponse(postsDtos, hasNext);
	}
}
