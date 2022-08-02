package prgrms.project.stuti.domain.feed.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import prgrms.project.stuti.domain.feed.model.Comment;
import prgrms.project.stuti.domain.feed.model.Feed;
import prgrms.project.stuti.domain.feed.repository.CommentRepository;
import prgrms.project.stuti.domain.feed.repository.FeedRepository;
import prgrms.project.stuti.domain.feed.service.dto.CommentCreateDto;
import prgrms.project.stuti.domain.feed.service.dto.CommentIdResponse;
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

	private Comment getParentComment(Long parentCommentId) {
		return commentRepository.findById(parentCommentId).orElseThrow(CommentException::PARENT_COMMENT_NOT_FOUND);
	}
}
