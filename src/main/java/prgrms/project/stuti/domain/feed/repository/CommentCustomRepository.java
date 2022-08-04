package prgrms.project.stuti.domain.feed.repository;

import java.util.List;

import prgrms.project.stuti.domain.feed.model.Comment;

public interface CommentCustomRepository {

	List<Comment> findAllByFeedIdAndParentIdIsNUllWithNoOffset(Long postId, Long lastCommentId, int size);
}
