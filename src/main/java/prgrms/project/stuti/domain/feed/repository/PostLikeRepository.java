package prgrms.project.stuti.domain.feed.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import prgrms.project.stuti.domain.feed.model.FeedLike;

public interface PostLikeRepository extends JpaRepository<FeedLike, Long> {

	Optional<FeedLike> findByFeedIdAndMemberId(Long postId, Long memberId);
}
