package prgrms.project.stuti.domain.studygroup.service.response;

import java.time.LocalDateTime;
import java.util.Set;

import lombok.Builder;
import prgrms.project.stuti.domain.member.model.Mbti;

public record StudyGroupDetailResponse(
	Long studyGroupId,
	String topic,
	String title,
	String imageUrl,
	StudyGroupMemberResponse leader,
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
	public StudyGroupDetailResponse {
	}
}
