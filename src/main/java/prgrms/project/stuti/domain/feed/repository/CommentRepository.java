package prgrms.project.stuti.domain.feed.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import prgrms.project.stuti.domain.feed.model.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long>, CommentCustomRepository{

	void deleteAllByFeedId(Long postId);

	boolean existsByIdLessThanAndParentIdNull(Long commentId);

	long countByFeedIdAndParentIdNull(Long postId);
}
