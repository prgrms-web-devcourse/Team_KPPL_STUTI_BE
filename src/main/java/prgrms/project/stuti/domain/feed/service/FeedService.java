package prgrms.project.stuti.domain.feed.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import prgrms.project.stuti.domain.feed.model.Feed;
import prgrms.project.stuti.domain.feed.model.FeedImage;
import prgrms.project.stuti.domain.feed.repository.CommentRepository;
import prgrms.project.stuti.domain.feed.repository.FeedImageRepository;
import prgrms.project.stuti.domain.feed.repository.FeedRepository;
import prgrms.project.stuti.domain.feed.service.dto.FeedResponse;
import prgrms.project.stuti.domain.feed.service.dto.PostChangeDto;
import prgrms.project.stuti.domain.feed.service.dto.PostCreateDto;
import prgrms.project.stuti.domain.feed.service.dto.PostDto;
import prgrms.project.stuti.domain.feed.service.dto.PostIdResponse;
import prgrms.project.stuti.domain.member.model.Member;
import prgrms.project.stuti.domain.member.repository.MemberRepository;
import prgrms.project.stuti.global.error.exception.FeedException;
import prgrms.project.stuti.global.error.exception.MemberException;
import prgrms.project.stuti.global.uploader.ImageUploader;
import prgrms.project.stuti.global.uploader.common.ImageDirectory;

@Service
@RequiredArgsConstructor
public class FeedService {

	private final FeedRepository feedRepository;
	private final MemberRepository memberRepository;
	private final ImageUploader imageUploader;
	private final FeedImageRepository feedImageRepository;
	private final CommentRepository commentRepository;

	@Transactional
	public PostIdResponse registerPost(PostCreateDto postDto) {
		Member findMember = memberRepository.findById(postDto.memberId())
			.orElseThrow(() -> MemberException.notFoundMember(postDto.memberId()));
		Feed feed = FeedConverter.toPost(postDto, findMember);
		Feed savedFeed = feedRepository.save(feed);

		String uploadUrl = imageUploader.upload(postDto.imageFile(), ImageDirectory.FEED);
		FeedImage feedImage = new FeedImage(uploadUrl, savedFeed);
		feedImageRepository.save(feedImage);

		return FeedConverter.toPostIdResponse(savedFeed.getId());
	}

	@Transactional(readOnly = true)
	public FeedResponse getAllPosts(Long lastPostId, int size) {
		List<PostDto> postsDtos = feedRepository.findAllWithNoOffset(lastPostId, size, null);
		if (!postsDtos.isEmpty()) {
			lastPostId = getLastPostId(postsDtos);
		}
		boolean hasNext = hasNext(lastPostId);

		return FeedConverter.toFeedResponse(postsDtos, hasNext);
	}

	@Transactional
	public PostIdResponse changePost(PostChangeDto postChangeDto) {
		Feed feed = feedRepository.findById(postChangeDto.postId()).orElseThrow(FeedException::FEED_NOT_FOUND);
		feed.changeContents(postChangeDto.contents());

		feedImageRepository.deleteByFeedId(feed.getId());
		if (postChangeDto.imageFile() != null) {
			String uploadUrl = imageUploader.upload(postChangeDto.imageFile(), ImageDirectory.FEED);
			FeedImage feedImage = new FeedImage(uploadUrl, feed);
			feedImageRepository.save(feedImage);
		}

		return FeedConverter.toPostIdResponse(feed.getId());
	}

	@Transactional
	public void deletePost(Long postId) {
		feedRepository.findById(postId).orElseThrow(FeedException::FEED_NOT_FOUND);
		feedImageRepository.deleteByFeedId(postId);
		commentRepository.deleteAllByFeedId(postId);
		feedRepository.deleteById(postId);
	}

	@Transactional(readOnly = true)
	public FeedResponse getMyPosts(Long memberId, Long lastPostId, int size) {
		List<PostDto> myPosts = feedRepository.findAllWithNoOffset(lastPostId, size, memberId);
		if (!myPosts.isEmpty()) {
			lastPostId = getLastPostId(myPosts);
		}
		boolean hasNext = hasNextMyPost(lastPostId, memberId);

		return FeedConverter.toFeedResponse(myPosts, hasNext);
	}

	private boolean hasNextMyPost(Long lastPostId, Long memberId) {
		if (lastPostId == null) {
			return false;
		}

		return feedRepository.existsByIdLessThanAndMemberId(lastPostId, memberId);
	}

	private boolean hasNext(Long lastPostId) {
		if (lastPostId == null) {
			return false;
		}

		return feedRepository.existsByIdLessThan(lastPostId);
	}

	private Long getLastPostId(List<PostDto> postDtos) {
		int lastIndex = postDtos.size() - 1;
		return postDtos.get(lastIndex).postId();
	}
}
