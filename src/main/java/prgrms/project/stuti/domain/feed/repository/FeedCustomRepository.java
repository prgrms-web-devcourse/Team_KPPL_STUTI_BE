package prgrms.project.stuti.domain.feed.repository;

import java.util.List;

import prgrms.project.stuti.domain.feed.service.dto.PostDto;

public interface FeedCustomRepository {

	List<PostDto> findAllWithNoOffset(Long lastPostId, int size);
}
