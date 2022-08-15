package prgrms.project.stuti.domain.post.repository.post;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import prgrms.project.stuti.domain.post.model.Post;

public interface PostRepository extends JpaRepository<Post, Long>, CustomPostRepository {

	boolean existsByIdLessThanAndDeletedFalse(Long lastPostId);

	boolean existsByIdLessThanAndMemberIdAndDeletedFalse(Long lastPostId, Long memberId);

	Optional<Post> findByIdAndDeletedFalse(Long postId);
}
