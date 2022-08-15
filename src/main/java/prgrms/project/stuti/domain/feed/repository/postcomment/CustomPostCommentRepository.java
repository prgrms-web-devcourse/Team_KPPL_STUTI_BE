package prgrms.project.stuti.domain.feed.repository.postcomment;

import prgrms.project.stuti.domain.feed.service.dto.PostCommentParentDto;
import prgrms.project.stuti.global.page.PageResponse;

public interface CustomPostCommentRepository {

	PageResponse<PostCommentParentDto> findAllByPostIdAndParentIdIsNUllWithNoOffset(Long postId, Long lastCommentId,
		int size);

	long totalParentComments(Long postId);
}
