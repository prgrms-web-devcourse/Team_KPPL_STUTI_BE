package prgrms.project.stuti.global.page.offset;

import java.util.List;

public record PageResponse <T>(
	List<T> contents,
	boolean hasNext,
	Long totalElements
) {
}
