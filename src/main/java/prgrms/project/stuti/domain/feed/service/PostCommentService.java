package prgrms.project.stuti.domain.feed.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import prgrms.project.stuti.domain.feed.model.Post;
import prgrms.project.stuti.domain.feed.model.PostComment;
import prgrms.project.stuti.domain.feed.repository.post.PostRepository;
import prgrms.project.stuti.domain.feed.repository.postcomment.PostCommentRepository;
import prgrms.project.stuti.domain.feed.service.dto.PostCommentParent;
import prgrms.project.stuti.domain.feed.service.dto.PostCommentChangeDto;
import prgrms.project.stuti.domain.feed.service.dto.PostCommentCreateDto;
import prgrms.project.stuti.domain.feed.service.dto.PostCommentGetDto;
import prgrms.project.stuti.domain.feed.service.response.PostCommentContentsResponse;
import prgrms.project.stuti.domain.feed.service.response.PostCommentResponse;
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
		if (postComment.getPost() == null) { //추후 isdelete로 변경시 로직확인 필요, 대댓글인 경우 댓글 있는지 확인 필요
			PostException.POST_NOT_FOUND(postCommentChangeDto.postId());
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
	public PageResponse<PostCommentParent> getPostComments(PostCommentGetDto postCommentGetDto) {
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
			.orElseThrow(() -> PostException.COMMENT_NOT_FOUND(postCommentId));
	}

	private PostComment getParentCommentById(Long parentPostCommentId) {
		return postCommentRepository.findById(parentPostCommentId)
			.orElseThrow(() -> PostException.PARENT_COMMENT_NOT_FOUND(parentPostCommentId));
	}

	private Post getPostById(Long postId) {
		return postRepository.findByIdAndDeletedFalse(postId).orElseThrow(() -> PostException.POST_NOT_FOUND(postId));
	}

	private Member getMemberById(Long memberId) {
		return memberRepository.findById(memberId).orElseThrow(() -> MemberException.notFoundMember(memberId));
	}

	private void validatePostById(Long postId) {
		postRepository.findByIdAndDeletedFalse(postId).orElseThrow(() -> PostException.POST_NOT_FOUND(postId));
	}

	private void validateEditMember(PostComment comment, Long editMemberId) {
		Member commentCreator = comment.getMember();
		Member commentEditor = getMemberById(editMemberId);
		if (!commentEditor.getId().equals(commentCreator.getId())) {
			PostException.INVALID_EDITOR(commentCreator.getId(), commentEditor.getId());
		}
	}
}
