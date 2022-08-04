package prgrms.project.stuti.domain.feed.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import prgrms.project.stuti.domain.feed.model.Comment;
import prgrms.project.stuti.domain.feed.model.Feed;
import prgrms.project.stuti.domain.feed.repository.CommentRepository;
import prgrms.project.stuti.domain.feed.repository.FeedRepository;
import prgrms.project.stuti.domain.feed.service.dto.CommentCreateDto;
import prgrms.project.stuti.domain.feed.service.dto.CommentGetDto;
import prgrms.project.stuti.domain.feed.service.dto.CommentIdResponse;
import prgrms.project.stuti.domain.feed.service.dto.CommentParentContents;
import prgrms.project.stuti.global.page.offset.PageResponse;
import prgrms.project.stuti.global.error.exception.CommentException;
import prgrms.project.stuti.global.error.exception.FeedException;

@Service
@RequiredArgsConstructor
public class CommentService {

	private final CommentRepository commentRepository;
	private final FeedRepository feedRepository;

	@Transactional
	public CommentIdResponse createComment(CommentCreateDto commentCreateDto) {
		Feed feed = feedRepository.findById(commentCreateDto.postId()).orElseThrow(FeedException::FEED_NOT_FOUND);
		Comment parentComment = null;
		if (commentCreateDto.parentId() != null) {
			parentComment = getParentComment(commentCreateDto.parentId());
		}
		Comment newComment = CommentConverter.toComment(commentCreateDto.contents(), feed, parentComment);
		Comment savedComment = commentRepository.save(newComment);

		return CommentConverter.toCommentIdResponse(savedComment.getId());
	}

	@Transactional(readOnly = true)
	public PageResponse getAllCommentsByPostId(CommentGetDto commentGetDto) {
		feedRepository.findById(commentGetDto.postId()).orElseThrow(FeedException::FEED_NOT_FOUND);
		List<Comment> comments = commentRepository.findAllByFeedIdAndParentIdIsNUllWithNoOffset(commentGetDto.postId(),
			commentGetDto.lastCommentId(), commentGetDto.size());
		boolean hasNext = hasNext(commentGetDto.lastCommentId());
		Long countParentCommentId = getCountParentCommentId(commentGetDto.postId());

		return CommentConverter.toCommentResponse(comments, hasNext, countParentCommentId);
	}

	private Comment getParentComment(Long parentCommentId) {
		return commentRepository.findById(parentCommentId).orElseThrow(CommentException::PARENT_COMMENT_NOT_FOUND);
	}

	private boolean hasNext(Long lastCommentId) {
		if (lastCommentId == null) {
			return false;
		}

		return commentRepository.existsByIdLessThanAndParentIdNull(lastCommentId);
	}

	private Long getCountParentCommentId(Long postId) {
		return commentRepository.countByFeedIdAndParentIdNull(postId);
	}

}
