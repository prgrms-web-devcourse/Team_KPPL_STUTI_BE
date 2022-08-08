package prgrms.project.stuti.domain.feed.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import prgrms.project.stuti.domain.feed.model.PostComment;

public interface PostCommentRepository extends JpaRepository<PostComment, Long>, PostCommentCustomRepository {

	void deleteAllByPostId(Long postId);

	void deleteAllByParentId(Long parentCommentId);

	boolean existsByIdLessThanAndParentIdNull(Long commentId);

	long countByPostIdAndParentIdNull(Long postId);
}
