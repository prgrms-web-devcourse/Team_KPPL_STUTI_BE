package prgrms.project.stuti.global.page.offset;

import java.util.List;

public record OffsetPageResponse<T>(List<T> contents, int page, int size, int totalPages, long totalElements,
									boolean sorted, boolean isFirst, boolean isLast) {
}
