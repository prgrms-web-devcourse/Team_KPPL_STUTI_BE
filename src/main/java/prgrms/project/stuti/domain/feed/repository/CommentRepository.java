package prgrms.project.stuti.domain.feed.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import prgrms.project.stuti.domain.feed.model.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {

	void deleteAllByFeedId(Long postId);
}
