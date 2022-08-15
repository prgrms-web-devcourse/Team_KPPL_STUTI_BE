package prgrms.project.stuti.domain.feed.repository.postcomment;

import prgrms.project.stuti.domain.feed.service.dto.PostCommentParent;
import prgrms.project.stuti.global.page.PageResponse;

public interface CustomPostCommentRepository {

	PageResponse<PostCommentParent> findAllByPostIdAndParentIdIsNUllWithNoOffset(Long postId, Long lastCommentId,
		int size);

	long totalParentComments(Long postId);
}
