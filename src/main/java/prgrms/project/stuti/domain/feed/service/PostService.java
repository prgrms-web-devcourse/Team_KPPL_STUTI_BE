package prgrms.project.stuti.domain.feed.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import prgrms.project.stuti.domain.feed.model.Post;
import prgrms.project.stuti.domain.feed.model.PostImage;
import prgrms.project.stuti.domain.feed.repository.PostCommentRepository;
import prgrms.project.stuti.domain.feed.repository.PostImageRepository;
import prgrms.project.stuti.domain.feed.repository.PostRepository;
import prgrms.project.stuti.domain.feed.service.dto.PostResponse;
import prgrms.project.stuti.domain.feed.service.dto.PostChangeDto;
import prgrms.project.stuti.domain.feed.service.dto.PostCreateDto;
import prgrms.project.stuti.domain.feed.service.dto.PostDto;
import prgrms.project.stuti.domain.feed.service.dto.PostIdResponse;
import prgrms.project.stuti.domain.member.model.Member;
import prgrms.project.stuti.domain.member.repository.MemberRepository;
import prgrms.project.stuti.global.error.exception.PostException;
import prgrms.project.stuti.global.error.exception.MemberException;
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

	@Transactional
	public PostIdResponse registerPost(PostCreateDto postDto) {
		Member findMember = memberRepository.findById(postDto.memberId())
			.orElseThrow(() -> MemberException.notFoundMember(postDto.memberId()));
		Post post = PostConverter.toPost(postDto, findMember);
		Post savedPost = postRepository.save(post);

		String uploadUrl = imageUploader.upload(postDto.imageFile(), ImageDirectory.POST);
		PostImage postImage = new PostImage(uploadUrl, savedPost);
		postImageRepository.save(postImage);

		return PostConverter.toPostIdResponse(savedPost.getId());
	}

	@Transactional(readOnly = true)
	public PostResponse getAllPosts(Long lastPostId, int size) {
		List<PostDto> postsDtos = postRepository.findAllWithNoOffset(lastPostId, size, null);
		if (!postsDtos.isEmpty()) {
			lastPostId = getLastPostId(postsDtos);
		}
		boolean hasNext = hasNext(lastPostId);

		return PostConverter.toPostResponse(postsDtos, hasNext);
	}

	@Transactional
	public PostIdResponse changePost(PostChangeDto postChangeDto) {
		Post post = postRepository.findById(postChangeDto.postId()).orElseThrow(PostException::POST_NOT_FOUND);
		post.changeContents(postChangeDto.contents());
		if(postChangeDto.imageFile() != null) {
			postImageRepository.deleteByPostId(post.getId());
			String uploadUrl = imageUploader.upload(postChangeDto.imageFile(), ImageDirectory.POST);
			PostImage postImage = new PostImage(uploadUrl, post);
			postImageRepository.save(postImage);
		}

		return PostConverter.toPostIdResponse(post.getId());
	}

	@Transactional
	public void deletePost(Long postId) {
		postRepository.findById(postId).orElseThrow(PostException::POST_NOT_FOUND);
		postImageRepository.deleteByPostId(postId);
		postCommentRepository.deleteAllByPostId(postId);
		postRepository.deleteById(postId);
	}

	@Transactional(readOnly = true)
	public PostResponse getMyPosts(Long memberId, Long lastPostId, int size) {
		List<PostDto> myPosts = postRepository.findAllWithNoOffset(lastPostId, size, memberId);
		if (!myPosts.isEmpty()) {
			lastPostId = getLastPostId(myPosts);
		}
		boolean hasNext = hasNextMyPost(lastPostId, memberId);

		return PostConverter.toPostResponse(myPosts, hasNext);
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

	private Long getLastPostId(List<PostDto> postDtos) {
		int lastIndex = postDtos.size() - 1;
		return postDtos.get(lastIndex).postId();
	}
}
