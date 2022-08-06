package prgrms.project.stuti.domain.studygroup.service.response;

import java.time.LocalDateTime;

import lombok.Builder;

public record StudyGroupQuestionResponse(
	Long studyGroupQuestionId,
	Long parentId,
	String profileImageUrl,
	Long memberId,
	String nickname,
	String contents,
	LocalDateTime updatedAt
) {

	@Builder
	public StudyGroupQuestionResponse {
	}
}
