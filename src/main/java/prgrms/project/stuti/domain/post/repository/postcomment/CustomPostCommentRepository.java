package prgrms.project.stuti.domain.post.repository.postcomment;

import prgrms.project.stuti.domain.post.service.dto.PostCommentParentDto;
import prgrms.project.stuti.global.page.PageResponse;

public interface CustomPostCommentRepository {

	PageResponse<PostCommentParentDto> findAllByPostIdAndParentIdIsNUllWithNoOffset(Long postId, Long lastCommentId,
		int size);

	long totalParentComments(Long postId);
}
