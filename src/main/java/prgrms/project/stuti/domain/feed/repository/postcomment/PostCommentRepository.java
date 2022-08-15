package prgrms.project.stuti.domain.feed.repository.postcomment;

import org.springframework.data.jpa.repository.JpaRepository;

import prgrms.project.stuti.domain.feed.model.PostComment;

public interface PostCommentRepository extends JpaRepository<PostComment, Long>, CustomPostCommentRepository {

	void deleteAllByPostId(Long postId);

	void deleteAllByParentId(Long parentCommentId);
}
