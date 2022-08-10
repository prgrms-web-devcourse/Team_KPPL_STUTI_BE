package prgrms.project.stuti.domain.feed.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import prgrms.project.stuti.domain.feed.model.Post;
import prgrms.project.stuti.domain.feed.model.PostLike;
import prgrms.project.stuti.domain.feed.repository.PostLikeRepository;
import prgrms.project.stuti.domain.feed.repository.PostRepository;
import prgrms.project.stuti.domain.feed.service.dto.PostLikeIdResponse;
import prgrms.project.stuti.domain.member.model.Member;
import prgrms.project.stuti.domain.member.repository.MemberRepository;
import prgrms.project.stuti.global.error.exception.MemberException;
import prgrms.project.stuti.global.error.exception.PostException;

@Service
@RequiredArgsConstructor
public class PostLikeService {

	private final MemberRepository memberRepository;
	private final PostRepository postRepository;
	private final PostLikeRepository postLikeRepository;

	@Transactional
	public PostLikeIdResponse createPostLike(Long postId, Long memberId) {
		Post post = postRepository.findById(postId).orElseThrow(PostException::POST_NOT_FOUND);
		Member member = memberRepository.findById(memberId).orElseThrow(() -> MemberException.notFoundMember(memberId));
		postLikeRepository.findByPostIdAndMemberId(postId, memberId)
			.ifPresent(l -> PostException.POST_LIKE_DUPLICATED());

		PostLike postLike = PostLikeConverter.toPostLike(member, post);
		PostLike savedPostLike = postLikeRepository.save(postLike);

		return PostLikeConverter.toPostLikeIdResponse(savedPostLike.getId());
	}

	@Transactional
	public void cancelPostLike(Long postId, Long memberId) {
		PostLike postLike = postLikeRepository.findByPostIdAndMemberId(postId, memberId)
			.orElseThrow(PostException::NOT_FOUND_POST_LIKE);
		postLikeRepository.deleteById(postLike.getId());
	}
}
