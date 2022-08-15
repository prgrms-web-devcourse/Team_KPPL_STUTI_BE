package prgrms.project.stuti.domain.post.service.response;

import java.util.List;

public record PostsResponse(
	List<PostDetailResponse> posts,
	boolean hasNext
) {
}
