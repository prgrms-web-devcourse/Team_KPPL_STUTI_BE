package prgrms.project.stuti.global.page.offset;

import java.util.List;

import lombok.Builder;
import prgrms.project.stuti.domain.feed.service.dto.CommentParentContents;

public record PageResponse(
	List<CommentParentContents> contents,
	boolean hasNext,
	Long totalElements
) {
	@Builder
	public PageResponse {
	}
}
