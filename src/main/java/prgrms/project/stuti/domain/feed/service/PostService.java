package prgrms.project.stuti.domain.feed.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import prgrms.project.stuti.domain.feed.model.Post;
import prgrms.project.stuti.domain.feed.model.PostImage;
import prgrms.project.stuti.domain.feed.repository.postcomment.PostCommentRepository;
import prgrms.project.stuti.domain.feed.repository.PostImageRepository;
import prgrms.project.stuti.domain.feed.repository.PostLikeRepository;
import prgrms.project.stuti.domain.feed.repository.post.PostRepository;
import prgrms.project.stuti.domain.feed.service.dto.PostChangeDto;
import prgrms.project.stuti.domain.feed.service.dto.PostCreateDto;
import prgrms.project.stuti.domain.feed.service.response.PostListResponse;
import prgrms.project.stuti.domain.feed.service.response.PostResponse;
import prgrms.project.stuti.domain.member.model.Member;
import prgrms.project.stuti.domain.member.repository.MemberRepository;
import prgrms.project.stuti.global.error.exception.MemberException;
import prgrms.project.stuti.global.error.exception.PostException;
import prgrms.project.stuti.global.uploader.ImageUploader;
import prgrms.project.stuti.global.uploader.common.ImageDirectory;

@Service
@RequiredArgsConstructor
public class PostService {

	private final PostRepository postRepository;
	private final MemberRepository memberRepository;
	private final ImageUploader imageUploader;
	private final PostImageRepository postImageRepository;
	private final PostCommentRepository postCommentRepository;
	private final PostLikeRepository postLikeRepository;

	@Transactional
	public PostResponse registerPost(PostCreateDto postDto) {
		Member findMember = memberRepository.findById(postDto.memberId())
			.orElseThrow(() -> MemberException.notFoundMember(postDto.memberId()));
		Post post = PostConverter.toPost(postDto, findMember);
		Post savedPost = postRepository.save(post);

		String uploadUrl = imageUploader.upload(postDto.imageFile(), ImageDirectory.POST);
		PostImage postImage = new PostImage(uploadUrl, savedPost);
		PostImage savedPostImage = postImageRepository.save(postImage);

		return PostConverter.toPostResponse(savedPost, findMember, savedPostImage, 0L, List.of());
	}

	@Transactional(readOnly = true)
	public PostListResponse getAllPosts(Long lastPostId, int size) {
		List<PostResponse> postsDtos = postRepository.findAllWithNoOffset(lastPostId, size, null);
		if (!postsDtos.isEmpty()) {
			lastPostId = getLastPostId(postsDtos);
		}
		boolean hasNext = hasNext(lastPostId);

		return PostConverter.toPostListResponse(postsDtos, hasNext);
	}

	@Transactional
	public PostResponse changePost(PostChangeDto postChangeDto) {
		Post post = postRepository.findById(postChangeDto.postId()).orElseThrow(PostException::POST_NOT_FOUND);
		post.changeContents(postChangeDto.contents());
		if (postChangeDto.imageFile() != null) {
			postImageRepository.deleteByPostId(post.getId());
			String uploadUrl = imageUploader.upload(postChangeDto.imageFile(), ImageDirectory.POST);
			PostImage postImage = new PostImage(uploadUrl, post);
			postImageRepository.save(postImage);
		}

		PostImage postImage = postImageRepository.findByPostId(post.getId()).get(0);
		Long totalParentComments = postCommentRepository.totalParentComments(post.getId());
		List<Long> allLikedMembers = postRepository.findAllLikedMembers(post.getId());

		return PostConverter.toPostResponse(post, post.getMember(), postImage, totalParentComments, allLikedMembers);
	}

	@Transactional
	public void deletePost(Long postId) {
		postRepository.findById(postId).orElseThrow(PostException::POST_NOT_FOUND);
		postImageRepository.deleteByPostId(postId);
		postCommentRepository.deleteAllByPostId(postId);
		postLikeRepository.deleteByPostId(postId);
		postRepository.deleteById(postId);
	}

	@Transactional(readOnly = true)
	public PostListResponse getMyPosts(Long memberId, Long lastPostId, int size) {
		List<PostResponse> myPosts = postRepository.findAllWithNoOffset(lastPostId, size, memberId);
		if (!myPosts.isEmpty()) {
			lastPostId = getLastPostId(myPosts);
		}
		boolean hasNext = hasNextMyPost(lastPostId, memberId);

		return PostConverter.toPostListResponse(myPosts, hasNext);
	}

	private boolean hasNextMyPost(Long lastPostId, Long memberId) {
		if (lastPostId == null) {
			return false;
		}

		return postRepository.existsByIdLessThanAndMemberId(lastPostId, memberId);
	}

	private boolean hasNext(Long lastPostId) {
		if (lastPostId == null) {
			return false;
		}

		return postRepository.existsByIdLessThan(lastPostId);
	}

	private Long getLastPostId(List<PostResponse> postResponses) {
		int lastIndex = postResponses.size() - 1;
		return postResponses.get(lastIndex).postId();
	}
}
