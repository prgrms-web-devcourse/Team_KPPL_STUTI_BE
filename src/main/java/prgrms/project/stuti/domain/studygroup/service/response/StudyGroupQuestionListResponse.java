package prgrms.project.stuti.domain.studygroup.service.response;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;

public record StudyGroupQuestionListResponse(
	Long studyGroupQuestionId,
	Long parentId,
	String profileImageUrl,
	Long memberId,
	String nickname,
	String contents,
	LocalDateTime updatedAt,
	List<StudyGroupQuestionResponse> children
) {

	@Builder
	public StudyGroupQuestionListResponse {
	}
}
