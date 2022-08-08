package prgrms.project.stuti.domain.feed.repository;

import prgrms.project.stuti.domain.feed.service.dto.CommentParentContents;
import prgrms.project.stuti.global.page.offset.PageResponse;

public interface CommentCustomRepository {

	PageResponse<CommentParentContents> findAllByFeedIdAndParentIdIsNUllWithNoOffset(Long postId, Long lastCommentId,
		int size);
}
