package prgrms.project.stuti.domain.feed.repository.post;

import org.springframework.data.jpa.repository.JpaRepository;

import prgrms.project.stuti.domain.feed.model.Post;

public interface PostRepository extends JpaRepository<Post, Long>, PostCustomRepository {

	boolean existsByIdLessThan(Long lastPostId);

	boolean existsByIdLessThanAndMemberId(Long lastPostId, Long memberId);
}
