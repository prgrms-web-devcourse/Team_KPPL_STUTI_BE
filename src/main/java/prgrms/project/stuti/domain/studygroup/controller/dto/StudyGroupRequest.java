package prgrms.project.stuti.domain.studygroup.controller.dto;

import java.time.LocalDateTime;
import java.util.Set;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import org.springframework.web.multipart.MultipartFile;

import prgrms.project.stuti.domain.member.model.Mbti;
import prgrms.project.stuti.domain.studygroup.controller.dto.validator.CustomDateTimeFormat;
import prgrms.project.stuti.domain.studygroup.controller.dto.validator.DescriptionLength;
import prgrms.project.stuti.domain.studygroup.controller.dto.validator.TitleLength;
import prgrms.project.stuti.domain.studygroup.model.Region;
import prgrms.project.stuti.domain.studygroup.model.StudyGroupMemberRole;
import prgrms.project.stuti.domain.studygroup.model.Topic;

public record StudyGroupRequest() {

	public static record CreateRequest(
		@NotBlank
		@TitleLength
		String title,

		@NotNull
		Topic topic,

		@NotNull
		boolean isOnline,

		@NotNull
		Region region,

		@NotNull
		@Positive
		int numberOfRecruits,

		@FutureOrPresent
		@CustomDateTimeFormat
		LocalDateTime startDateTime,

		@Future
		@CustomDateTimeFormat
		LocalDateTime endDateTime,

		Set<Mbti> preferredMBTIs,

		MultipartFile imageFile,

		@NotBlank
		@DescriptionLength
		String description
	) {
	}

	public static record FindCondition(
		Mbti mbti,
		Topic topic,
		Region region,
		StudyGroupMemberRole studyGroupMemberRole,
		Long lastStudyGroupId
	) {
	}

	public static record UpdateRequest(
		@NotBlank
		@TitleLength
		String title,

		MultipartFile imageFile,

		@NotBlank
		@DescriptionLength
		String description
	) {
	}
}
