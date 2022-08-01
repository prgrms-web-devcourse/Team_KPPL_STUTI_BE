package prgrms.project.stuti.domain.studygroup.controller.dto;

import java.time.LocalDateTime;
import java.util.List;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.web.multipart.MultipartFile;

import lombok.Builder;
import prgrms.project.stuti.domain.member.model.Mbti;
import prgrms.project.stuti.domain.studygroup.controller.dto.validator.CustomDateTimeFormat;
import prgrms.project.stuti.domain.studygroup.controller.dto.validator.DescriptionLength;
import prgrms.project.stuti.domain.studygroup.controller.dto.validator.TitleLength;
import prgrms.project.stuti.domain.studygroup.model.Region;
import prgrms.project.stuti.domain.studygroup.model.Topic;

public record StudyGroupCreateRequest(
	@NotNull
	MultipartFile imageFile,

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
	List<Mbti> preferredMBTIs,

	@NotNull
	@Positive
	int numberOfRecruits,

	@FutureOrPresent
	@CustomDateTimeFormat
	LocalDateTime startDateTime,

	@Future
	@CustomDateTimeFormat
	LocalDateTime endDateTime,

	@NotBlank
	@DescriptionLength
	String description
) {

	@Builder
	public StudyGroupCreateRequest {
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this,
			ToStringStyle.SHORT_PREFIX_STYLE)
			.append("imageFile", imageFile)
			.append("title", title)
			.append("topic", topic)
			.append("isOnline", isOnline)
			.append("region", region)
			.append("preferredMBTIs", preferredMBTIs)
			.append("startDateTime", startDateTime)
			.append("endDateTime", endDateTime)
			.append("description", description)
			.toString();
	}
}
