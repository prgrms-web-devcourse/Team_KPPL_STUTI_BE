package prgrms.project.stuti.domain.feed.service.dto;

import java.util.List;

public record PostResponse(
	List<PostDto> posts,
	boolean hasNext
) {
}
