package prgrms.project.stuti.global.page;

import java.util.List;

public record CursorPageResponse<T>(
	List<T> contents,
	boolean hasNext
) {
}
