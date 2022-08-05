package prgrms.project.stuti.domain.feed.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import prgrms.project.stuti.domain.feed.controller.PostLikeController;
import prgrms.project.stuti.domain.feed.model.Feed;
import prgrms.project.stuti.domain.feed.model.FeedLike;
import prgrms.project.stuti.domain.feed.repository.FeedRepository;
import prgrms.project.stuti.domain.feed.repository.PostLikeRepository;
import prgrms.project.stuti.domain.feed.service.dto.PostLikeIdResponse;
import prgrms.project.stuti.domain.member.model.Member;
import prgrms.project.stuti.domain.member.repository.MemberRepository;
import prgrms.project.stuti.global.error.exception.FeedException;
import prgrms.project.stuti.global.error.exception.MemberException;

@Service
@RequiredArgsConstructor
public class PostLikeService {

	private final MemberRepository memberRepository;
	private final FeedRepository postRepository;
	private final PostLikeRepository postLikeRepository;

	@Transactional
	public PostLikeIdResponse createPostLike(Long postId, Long memberId) {
		Feed feed = postRepository.findById(postId).orElseThrow(FeedException::FEED_NOT_FOUND);
		Member member = memberRepository.findById(memberId).orElseThrow(() -> MemberException.notFoundMember(memberId));
		postLikeRepository.findByFeedIdAndMemberId(postId, memberId)
			.ifPresent(l -> FeedException.FEED_LIKE_DUPLICATED());

		FeedLike feedLike = PostLikeConverter.toFeedLike(member, feed);
		FeedLike savedPostLike = postLikeRepository.save(feedLike);

		return PostLikeConverter.toPostLikeIdResponse(savedPostLike.getId());
	}
}
