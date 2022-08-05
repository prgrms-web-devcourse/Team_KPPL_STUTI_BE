package prgrms.project.stuti.domain.feed.service;

import prgrms.project.stuti.domain.feed.model.Feed;
import prgrms.project.stuti.domain.feed.model.FeedLike;
import prgrms.project.stuti.domain.feed.service.dto.PostLikeIdResponse;
import prgrms.project.stuti.domain.member.model.Member;

public class PostLikeConverter {

	public static FeedLike toFeedLike(Member member, Feed feed) {
		return new FeedLike(member, feed);
	}

	public static PostLikeIdResponse toPostLikeIdResponse(Long postLikeId) {
		return new PostLikeIdResponse(postLikeId);
	}
}
