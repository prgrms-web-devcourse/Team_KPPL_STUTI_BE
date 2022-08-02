package prgrms.project.stuti.domain.feed.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import prgrms.project.stuti.domain.feed.model.FeedImage;

public interface FeedImageRepository extends JpaRepository<FeedImage, Long> {

	List<FeedImage> findByFeedId(Long postId);

	void deleteByFeedId(Long postId);
}
