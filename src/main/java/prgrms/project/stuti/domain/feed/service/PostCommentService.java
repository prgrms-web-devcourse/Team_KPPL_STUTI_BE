package prgrms.project.stuti.domain.feed.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import prgrms.project.stuti.domain.feed.model.PostComment;
import prgrms.project.stuti.domain.feed.model.Post;
import prgrms.project.stuti.domain.feed.repository.PostCommentRepository;
import prgrms.project.stuti.domain.feed.repository.PostRepository;
import prgrms.project.stuti.domain.feed.service.dto.PostCommentContentsResponse;
import prgrms.project.stuti.domain.feed.service.dto.PostCommentCreateDto;
import prgrms.project.stuti.domain.feed.service.dto.PostCommentGetDto;
import prgrms.project.stuti.domain.feed.service.dto.CommentParentContents;
import prgrms.project.stuti.domain.feed.service.dto.PostCommentResponse;
import prgrms.project.stuti.domain.feed.service.dto.PostCommentUpdateDto;
import prgrms.project.stuti.global.error.exception.CommentException;
import prgrms.project.stuti.global.error.exception.PostException;
import prgrms.project.stuti.global.page.offset.PageResponse;

@Service
@RequiredArgsConstructor
public class PostCommentService {

	private final PostCommentRepository postCommentRepository;
	private final PostRepository postRepository;

	@Transactional
	public PostCommentResponse createComment(PostCommentCreateDto postCommentCreateDto) {
		Post post = postRepository.findById(postCommentCreateDto.postId()).orElseThrow(PostException::POST_NOT_FOUND);
		PostComment parentPostComment = null;
		if (postCommentCreateDto.parentId() != null) {
			parentPostComment = getParentComment(postCommentCreateDto.parentId());
		}
		PostComment newPostComment = PostCommentConverter.toComment(postCommentCreateDto.contents(), post, parentPostComment);
		PostComment savedPostComment = postCommentRepository.save(newPostComment);

		return PostCommentConverter.toCommentResponse(savedPostComment);
	}

	@Transactional
	public PostCommentResponse changeComment(PostCommentUpdateDto postCommentUpdateDto) {
		PostComment postComment = postCommentRepository.findById(postCommentUpdateDto.postCommentId())
			.orElseThrow(() -> CommentException.COMMENT_NOT_FOUND(postCommentUpdateDto.postCommentId()));
		if (postComment.getPost() == null) { //추후 isdelete로 변경시 로직확인 필요, 대댓글인 경우 댓글 있는지 확인 필요
			PostException.POST_NOT_FOUND();
		}
		postComment.changeContents(postCommentUpdateDto.contents());

		return PostCommentConverter.toCommentResponse(postComment);
	}

	@Transactional
	public void deleteComment(Long postId, Long commentId, Long memberId) {
		postRepository.findById(postId).orElseThrow(PostException::POST_NOT_FOUND);
		PostComment foundPostComment = postCommentRepository.findById(commentId)
			.orElseThrow(() -> CommentException.COMMENT_NOT_FOUND(commentId));
		deleteComments(foundPostComment);
	}

	@Transactional(readOnly = true)
	public PageResponse<CommentParentContents> getPostComments(PostCommentGetDto postCommentGetDto) {
		postRepository.findById(postCommentGetDto.postId()).orElseThrow(PostException::POST_NOT_FOUND);

		return postCommentRepository.findAllByPostIdAndParentIdIsNUllWithNoOffset(
			postCommentGetDto.postId(),
			postCommentGetDto.lastCommentId(), postCommentGetDto.size());
	}

	@Transactional(readOnly = true)
	public PostCommentContentsResponse getCommentContents(Long postId, Long commentId) {
		postRepository.findById(postId).orElseThrow(PostException::POST_NOT_FOUND);
		PostComment postComment = postCommentRepository.findById(commentId)
			.orElseThrow(() -> CommentException.COMMENT_NOT_FOUND(commentId));

		return PostCommentConverter.toCommentContentsResponse(postComment);
	}

	private PostComment getParentComment(Long parentCommentId) {
		return postCommentRepository.findById(parentCommentId)
			.orElseThrow(() -> CommentException.PARENT_COMMENT_NOT_FOUND(parentCommentId));
	}

	private void deleteComments(PostComment deletePostComment) {
		if (deletePostComment.getParent() == null) {
			postCommentRepository.deleteAllByParentId(deletePostComment.getId());
		}
		postCommentRepository.delete(deletePostComment);
	}
}
