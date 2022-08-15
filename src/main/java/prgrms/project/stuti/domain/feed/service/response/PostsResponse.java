package prgrms.project.stuti.domain.feed.service.response;

import java.util.List;

public record PostsResponse(
	List<PostDetailResponse> posts,
	boolean hasNext
) {
}
