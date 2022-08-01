package prgrms.project.stuti.domain.feed.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import prgrms.project.stuti.domain.feed.model.Feed;

public interface FeedRepository extends JpaRepository<Feed, Long>, FeedCustomRepository {

	boolean existsByIdLessThan(Long lastPostId);

	boolean existsByIdGreaterThanEqual(Long lastPostId);
}
