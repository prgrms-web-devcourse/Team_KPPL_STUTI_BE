package prgrms.project.stuti.global.page;

import java.util.List;

public record PageResponse<T>(
	List<T> contents,
	boolean hasNext,
	long totalElements
) {
}
