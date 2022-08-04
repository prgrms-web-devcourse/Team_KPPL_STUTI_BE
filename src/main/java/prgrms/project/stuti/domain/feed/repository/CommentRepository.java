package prgrms.project.stuti.domain.feed.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import prgrms.project.stuti.domain.feed.model.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long>, CommentCustomRepository{

	boolean existsByIdLessThanAndParentIdNull(Long commentId);

	long countByFeedIdAndParentIdNull(Long postId);
}
