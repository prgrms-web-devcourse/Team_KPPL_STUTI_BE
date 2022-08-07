package prgrms.project.stuti.domain.feed.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import prgrms.project.stuti.domain.feed.model.Comment;
import prgrms.project.stuti.domain.feed.model.Feed;
import prgrms.project.stuti.domain.feed.repository.CommentRepository;
import prgrms.project.stuti.domain.feed.repository.FeedRepository;
import prgrms.project.stuti.domain.feed.service.dto.CommentContentsResponse;
import prgrms.project.stuti.domain.feed.service.dto.CommentCreateDto;
import prgrms.project.stuti.domain.feed.service.dto.CommentGetDto;
import prgrms.project.stuti.domain.feed.service.dto.CommentParentContents;
import prgrms.project.stuti.domain.feed.service.dto.CommentResponse;
import prgrms.project.stuti.domain.feed.service.dto.CommentUpdateDto;
import prgrms.project.stuti.global.error.exception.CommentException;
import prgrms.project.stuti.global.error.exception.FeedException;
import prgrms.project.stuti.global.page.offset.PageResponse;

@Service
@RequiredArgsConstructor
public class CommentService {

	private final CommentRepository commentRepository;
	private final FeedRepository feedRepository;

	@Transactional
	public CommentResponse createComment(CommentCreateDto commentCreateDto) {
		Feed feed = feedRepository.findById(commentCreateDto.postId()).orElseThrow(FeedException::FEED_NOT_FOUND);
		Comment parentComment = null;
		if (commentCreateDto.parentId() != null) {
			parentComment = getParentComment(commentCreateDto.parentId());
		}
		Comment newComment = CommentConverter.toComment(commentCreateDto.contents(), feed, parentComment);
		Comment savedComment = commentRepository.save(newComment);

		return CommentConverter.toCommentResponse(savedComment);
	}

	@Transactional
	public CommentResponse changeComment(CommentUpdateDto commentUpdateDto) {
		Comment comment = commentRepository.findById(commentUpdateDto.postCommentId())
			.orElseThrow(() -> CommentException.COMMENT_NOT_FOUND(commentUpdateDto.postCommentId()));
		if (comment.getFeed() == null) { //추후 isdelete로 변경시 로직확인 필요, 대댓글인 경우 댓글 있는지 확인 필요
			FeedException.FEED_NOT_FOUND();
		}
		comment.changeContents(commentUpdateDto.contents());

		return CommentConverter.toCommentResponse(comment);
	}

	@Transactional
	public void deleteComment(Long postId, Long commentId, Long memberId) {
		feedRepository.findById(postId).orElseThrow(FeedException::FEED_NOT_FOUND);
		Comment foundComment = commentRepository.findById(commentId)
			.orElseThrow(() -> CommentException.COMMENT_NOT_FOUND(commentId));
		deleteComments(foundComment);
	}

	@Transactional(readOnly = true)
	public PageResponse<CommentParentContents> getPostComments(CommentGetDto commentGetDto) {
		feedRepository.findById(commentGetDto.postId()).orElseThrow(FeedException::FEED_NOT_FOUND);

		return commentRepository.findAllByFeedIdAndParentIdIsNUllWithNoOffset(
			commentGetDto.postId(),
			commentGetDto.lastCommentId(), commentGetDto.size());
	}

	@Transactional(readOnly = true)
	public CommentContentsResponse getCommentContents(Long postId, Long commentId) {
		feedRepository.findById(postId).orElseThrow(FeedException::FEED_NOT_FOUND);
		Comment comment = commentRepository.findById(commentId)
			.orElseThrow(() -> CommentException.COMMENT_NOT_FOUND(commentId));

		return CommentConverter.toCommentContentsResponse(comment);
	}

	private Comment getParentComment(Long parentCommentId) {
		return commentRepository.findById(parentCommentId)
			.orElseThrow(() -> CommentException.PARENT_COMMENT_NOT_FOUND(parentCommentId));
	}

	private void deleteComments(Comment deleteComment) {
		if (deleteComment.getParent() == null) {
			commentRepository.deleteAllByParentId(deleteComment.getId());
		}
		commentRepository.delete(deleteComment);
	}
}
