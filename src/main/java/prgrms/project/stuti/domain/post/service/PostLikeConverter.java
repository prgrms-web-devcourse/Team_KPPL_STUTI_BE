package prgrms.project.stuti.domain.post.service;

import prgrms.project.stuti.domain.post.model.Post;
import prgrms.project.stuti.domain.post.model.PostLike;
import prgrms.project.stuti.domain.post.service.response.PostLikeIdResponse;
import prgrms.project.stuti.domain.member.model.Member;

public class PostLikeConverter {

	public static PostLike toPostLike(Member member, Post post) {
		return new PostLike(member, post);
	}

	public static PostLikeIdResponse toPostLikeIdResponse(Long postLikeId) {
		return new PostLikeIdResponse(postLikeId);
	}
}
