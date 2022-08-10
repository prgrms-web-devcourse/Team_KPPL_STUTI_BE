package prgrms.project.stuti.domain.feed.service.dto;

import java.util.List;

public record PostListResponse(
	List<PostResponse> posts,
	boolean hasNext
) {
}
