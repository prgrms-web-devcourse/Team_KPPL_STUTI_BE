package prgrms.project.stuti.domain.feed.repository;

import java.util.List;

import prgrms.project.stuti.domain.feed.service.dto.PostResponse;

public interface PostCustomRepository {

	List<PostResponse> findAllWithNoOffset(Long lastPostId, int size, Long memberId);

	List<Long> findAllLikedMembers(Long postId);
}
