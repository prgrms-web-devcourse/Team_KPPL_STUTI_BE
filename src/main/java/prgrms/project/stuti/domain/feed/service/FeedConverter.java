package prgrms.project.stuti.domain.feed.service;

import prgrms.project.stuti.domain.feed.model.Feed;
import prgrms.project.stuti.domain.feed.service.dto.PostCreateDto;
import prgrms.project.stuti.domain.member.model.Member;

public class FeedConverter {

	public static Feed toPost(PostCreateDto postDto, Member member) {
		return new Feed(postDto.contents(), member);
	}
}
