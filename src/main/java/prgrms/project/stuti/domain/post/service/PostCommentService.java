package prgrms.project.stuti.domain.post.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import prgrms.project.stuti.domain.post.model.Post;
import prgrms.project.stuti.domain.post.model.PostComment;
import prgrms.project.stuti.domain.post.repository.post.PostRepository;
import prgrms.project.stuti.domain.post.repository.postcomment.PostCommentRepository;
import prgrms.project.stuti.domain.post.service.dto.PostCommentChangeDto;
import prgrms.project.stuti.domain.post.service.dto.PostCommentCreateDto;
import prgrms.project.stuti.domain.post.service.dto.PostCommentGetDto;
import prgrms.project.stuti.domain.post.service.dto.PostCommentParentDto;
import prgrms.project.stuti.domain.post.service.response.PostCommentContentsResponse;
import prgrms.project.stuti.domain.post.service.response.PostCommentResponse;
import prgrms.project.stuti.domain.member.model.Member;
import prgrms.project.stuti.domain.member.repository.MemberRepository;
import prgrms.project.stuti.global.error.exception.MemberException;
import prgrms.project.stuti.global.error.exception.PostException;
import prgrms.project.stuti.global.page.PageResponse;

@Service
@RequiredArgsConstructor
public class PostCommentService {

	private final PostCommentRepository postCommentRepository;
	private final PostRepository postRepository;
	private final MemberRepository memberRepository;

	@Transactional
	public PostCommentResponse createComment(PostCommentCreateDto postCommentCreateDto) {
		Post post = getPostById(postCommentCreateDto.postId());
		Member foundMember = getMemberById(postCommentCreateDto.memberId());
		PostComment parentPostComment = null;
		if (postCommentCreateDto.parentId() != null) {
			parentPostComment = getParentCommentById(postCommentCreateDto.parentId());
		}
		PostComment newPostComment = PostCommentConverter.toPostComment(postCommentCreateDto.contents(), post,
			parentPostComment, foundMember);
		PostComment savedPostComment = postCommentRepository.save(newPostComment);

		return PostCommentConverter.toPostCommentResponse(savedPostComment);
	}

	@Transactional
	public PostCommentResponse changeComment(PostCommentChangeDto postCommentChangeDto) {
		PostComment postComment = getCommentById(postCommentChangeDto.postCommentId());
		validateEditMember(postComment, postCommentChangeDto.memberId());
		if (postComment.getPost() == null) {
			throw PostException.notFoundPost(postCommentChangeDto.postId());
		}
		postComment.changeContents(postCommentChangeDto.contents());

		return PostCommentConverter.toPostCommentResponse(postComment);
	}

	@Transactional
	public PostCommentResponse deleteComment(Long postId, Long commentId, Long memberId) {
		validatePostById(postId);
		PostComment foundPostComment = getCommentById(commentId);
		validateEditMember(foundPostComment, memberId);

		deleteComments(foundPostComment);

		return PostCommentConverter.toPostCommentResponse(foundPostComment);
	}

	@Transactional(readOnly = true)
	public PageResponse<PostCommentParentDto> getPostComments(PostCommentGetDto postCommentGetDto) {
		getPostById(postCommentGetDto.postId());

		return postCommentRepository.findAllByPostIdAndParentIdIsNUllWithNoOffset(postCommentGetDto.postId(),
			postCommentGetDto.lastCommentId(), postCommentGetDto.size());
	}

	@Transactional(readOnly = true)
	public PostCommentContentsResponse getCommentContents(Long postId, Long commentId) {
		validatePostById(postId);
		PostComment postComment = getCommentById(commentId);

		return PostCommentConverter.toPostCommentContentsResponse(postComment);
	}

	private void deleteComments(PostComment deletePostComment) {
		if (deletePostComment.getParent() == null) {
			postCommentRepository.deleteAllByParentId(deletePostComment.getId());
		}
		postCommentRepository.delete(deletePostComment);
	}

	private PostComment getCommentById(Long postCommentId) {
		return postCommentRepository.findById(postCommentId)
			.orElseThrow(() -> PostException.notFoundComment(postCommentId));
	}

	private PostComment getParentCommentById(Long parentPostCommentId) {
		return postCommentRepository.findById(parentPostCommentId)
			.orElseThrow(() -> PostException.notFoundParentComment(parentPostCommentId));
	}

	private Post getPostById(Long postId) {
		return postRepository.findByIdAndIsDeletedFalse(postId).orElseThrow(() -> PostException.notFoundPost(postId));
	}

	private Member getMemberById(Long memberId) {
		return memberRepository.findById(memberId).orElseThrow(() -> MemberException.notFoundMember(memberId));
	}

	private void validatePostById(Long postId) {
		postRepository.findByIdAndIsDeletedFalse(postId).orElseThrow(() -> PostException.notFoundPost(postId));
	}

	private void validateEditMember(PostComment comment, Long editMemberId) {
		Member commentCreator = comment.getMember();
		Member commentEditor = getMemberById(editMemberId);
		if (!commentEditor.getId().equals(commentCreator.getId())) {
			throw PostException.invalidEditor(commentCreator.getId(), commentEditor.getId());
		}
	}
}
