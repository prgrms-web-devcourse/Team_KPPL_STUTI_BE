package prgrms.project.stuti.domain.feed.repository;

import java.util.List;

import prgrms.project.stuti.domain.feed.service.dto.PostDto;

public interface PostCustomRepository {

	List<PostDto> findAllWithNoOffset(Long lastPostId, int size, Long memberId);
}
