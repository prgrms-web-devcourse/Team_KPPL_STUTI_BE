package prgrms.project.stuti.domain.feed.repository.post;

import java.util.List;

import prgrms.project.stuti.domain.feed.service.response.PostDetailResponse;

public interface CustomPostRepository {

	List<PostDetailResponse> findAllWithNoOffset(Long lastPostId, int size, Long memberId);

	List<Long> findAllLikedMembers(Long postId);
}
