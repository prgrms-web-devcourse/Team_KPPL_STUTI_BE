package prgrms.project.stuti.domain.post.repository.post;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import prgrms.project.stuti.domain.post.model.Post;

public interface PostRepository extends JpaRepository<Post, Long>, CustomPostRepository {

	boolean existsByIdLessThanAndIsDeletedFalse(Long lastPostId);

	boolean existsByIdLessThanAndMemberIdAndIsDeletedFalse(Long lastPostId, Long memberId);

	Optional<Post> findByIdAndIsDeletedFalse(Long postId);
}
