package prgrms.project.stuti.domain.studygroup.service.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import prgrms.project.stuti.domain.member.model.Career;
import prgrms.project.stuti.domain.member.model.Field;
import prgrms.project.stuti.domain.member.model.Mbti;
import prgrms.project.stuti.domain.studygroup.model.Region;
import prgrms.project.stuti.domain.studygroup.model.Topic;

public record StudyGroupDetailDto(
	Long studyGroupId,
	Topic topic,
	String title,
	String imageUrl,
	boolean isOnline,
	Region region,
	LocalDateTime startDateTime,
	LocalDateTime endDateTime,
	int numberOfMembers,
	int numberOfRecruits,
	String description,
	Long memberId,
	String profileImageUrl,
	String nickname,
	Field field,
	Career career,
	Mbti mbti,
	Mbti preferredMBTI
) {

	@Builder
	public StudyGroupDetailDto {
	}
}
