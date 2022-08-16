package prgrms.project.stuti.domain.post.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import prgrms.project.stuti.domain.post.model.PostLike;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

	Optional<PostLike> findByPostIdAndMemberId(Long postId, Long memberId);

	void deleteAllByPostId(Long postId);
}
