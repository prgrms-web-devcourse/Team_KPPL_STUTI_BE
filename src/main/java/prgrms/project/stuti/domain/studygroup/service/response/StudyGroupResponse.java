package prgrms.project.stuti.domain.studygroup.service.response;

import java.time.LocalDateTime;
import java.util.Set;

import lombok.Builder;
import prgrms.project.stuti.domain.member.model.Mbti;

public record StudyGroupResponse(
	Long studyGroupId,
	String topic,
	String title,
	String imageUrl,
	StudyGroupLeader leader,
	Set<Mbti> preferredMBTIs,
	boolean isOnline,
	String region,
	LocalDateTime startDateTime,
	LocalDateTime endDateTime,
	int numberOfMembers,
	int numberOfRecruits,
	String description
) {

	@Builder
	public StudyGroupResponse {
	}

	public static record StudyGroupLeader(
		Long memberId,
		String profileImageUrl,
		String nickname,
		String field,
		String career,
		Mbti mbti
	) {

		@Builder
		public StudyGroupLeader {
		}
	}
}
