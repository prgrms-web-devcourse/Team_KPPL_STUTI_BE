package prgrms.project.stuti.domain.feed.service.dto;

import java.util.List;

public record PostsResponse(
	List<PostDto> posts,
	boolean hasNext
) {
}
