package prgrms.project.stuti.domain.studygroup.service.response;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;

public record StudyGroupDetailResponse(
	Long studyGroupId,
	String topic,
	String title,
	String imageUrl,
	LeaderResponse leader,
	List<String> preferredMBTIs,
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
