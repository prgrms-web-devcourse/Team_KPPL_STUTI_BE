package prgrms.project.stuti.domain.feed.repository.postcomment;

import prgrms.project.stuti.domain.feed.service.dto.CommentParentContents;
import prgrms.project.stuti.global.page.PageResponse;

public interface PostCommentCustomRepository {

	PageResponse<CommentParentContents> findAllByPostIdAndParentIdIsNUllWithNoOffset(Long postId, Long lastCommentId,
		int size);

	long totalParentComments(Long postId);
}
