package prgrms.project.stuti.domain.studygroup.service.dto;

import java.time.LocalDateTime;
import java.util.Set;

import org.springframework.web.multipart.MultipartFile;

import lombok.Builder;
import prgrms.project.stuti.domain.member.model.Mbti;
import prgrms.project.stuti.domain.studygroup.model.Region;
import prgrms.project.stuti.domain.studygroup.model.StudyGroupMemberRole;
import prgrms.project.stuti.domain.studygroup.model.Topic;

public record StudyGroupDto() {

	public static record CreateDto(
		Long memberId,
		String title,
		Topic topic,
		boolean isOnline,
		Region region,
		int numberOfRecruits,
		LocalDateTime startDateTime,
		LocalDateTime endDateTime,
		Set<Mbti> preferredMBTIs,
		MultipartFile imageFile,
		String description
	) {

		@Builder
		public CreateDto {
		}
	}

	public static record ReadDto(Long studyGroupId) {
	}

	public static record FindCondition(
		Mbti mbti,
		Topic topic,
		Region region,
		StudyGroupMemberRole studyGroupMemberRole,
		Long memberId,
		Long lastStudyGroupId,
		Long size
	) {

		@Builder
		public FindCondition {
		}
	}

	public static record UpdateDto(
		Long memberId,
		Long studyGroupId,
		String title,
		MultipartFile imageFile,
		String description
	) {

		@Builder
		public UpdateDto {
		}
	}

	public static record DeleteDto(Long memberId, Long studyGroupId) {
	}
}
