package prgrms.project.stuti.domain.post.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import prgrms.project.stuti.domain.post.model.Post;
import prgrms.project.stuti.domain.post.model.PostImage;
import prgrms.project.stuti.domain.post.repository.postcomment.PostCommentRepository;
import prgrms.project.stuti.domain.post.repository.PostImageRepository;
import prgrms.project.stuti.domain.post.repository.PostLikeRepository;
import prgrms.project.stuti.domain.post.repository.post.PostRepository;
import prgrms.project.stuti.domain.post.service.dto.PostChangeDto;
import prgrms.project.stuti.domain.post.service.dto.PostCreateDto;
import prgrms.project.stuti.domain.post.service.response.PostsResponse;
import prgrms.project.stuti.domain.post.service.response.PostDetailResponse;
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
	public PostDetailResponse registerPost(PostCreateDto postDto) {
		Member findMember = memberRepository.findById(postDto.memberId())
			.orElseThrow(() -> MemberException.notFoundMember(postDto.memberId()));
		Post post = PostConverter.toPost(postDto, findMember);
		Post savedPost = postRepository.save(post);

		String uploadUrl = imageUploader.upload(postDto.imageFile(), ImageDirectory.POST);
		PostImage postImage = new PostImage(uploadUrl, savedPost);
		PostImage savedPostImage = postImageRepository.save(postImage);

		return PostConverter.toPostDetailResponse(savedPost, findMember, savedPostImage, 0L, List.of());
	}

	@Transactional(readOnly = true)
	public PostsResponse getAllPosts(Long lastPostId, int size) {
		List<PostDetailResponse> postsDtos = postRepository.findAllWithNoOffset(lastPostId, size, null);
		boolean hasNext = false;
		if (postsDtos.size() == size) {
			lastPostId = getLastPostId(postsDtos);
			hasNext = hasNext(lastPostId);
		}

		return PostConverter.toPostsResponse(postsDtos, hasNext);
	}

	@Transactional
	public PostDetailResponse changePost(PostChangeDto postChangeDto) {
		Post post = postRepository.findByIdAndIsDeletedFalse(postChangeDto.postId())
			.orElseThrow(() -> PostException.notFoundPost(postChangeDto.postId()));

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

		return PostConverter.toPostDetailResponse(post, post.getMember(), postImage, totalParentComments,
			allLikedMembers);
	}

	@Transactional
	public void deletePost(Long postId) {
		Post post = postRepository.findByIdAndIsDeletedFalse(postId)
			.orElseThrow(() -> PostException.notFoundPost(postId));

		softDeletePost(post);
	}

	@Transactional(readOnly = true)
	public PostsResponse getMemberPosts(Long memberId, Long lastPostId, int size) {
		List<PostDetailResponse> myPosts = postRepository.findAllWithNoOffset(lastPostId, size, memberId);
		boolean hasNext = false;
		if (myPosts.size() == size) {
			lastPostId = getLastPostId(myPosts);
			hasNext = hasNextMyPost(lastPostId, memberId);
		}

		return PostConverter.toPostsResponse(myPosts, hasNext);
	}

	private boolean hasNextMyPost(Long lastPostId, Long memberId) {
		if (lastPostId == null) {
			return false;
		}

		return postRepository.existsByIdLessThanAndMemberIdAndIsDeletedFalse(lastPostId, memberId);
	}

	private boolean hasNext(Long lastPostId) {
		if (lastPostId == null) {
			return false;
		}

		return postRepository.existsByIdLessThanAndIsDeletedFalse(lastPostId);
	}

	private Long getLastPostId(List<PostDetailResponse> postDetailResponses) {
		int lastIndex = postDetailResponses.size() - 1;
		return postDetailResponses.get(lastIndex).postId();
	}

	private void softDeletePost(Post post) {
		postImageRepository.deleteByPostId(post.getId());
		postCommentRepository.deleteAllByPostId(post.getId());
		postLikeRepository.deleteAllByPostId(post.getId());
		post.softDelete();
	}
}
