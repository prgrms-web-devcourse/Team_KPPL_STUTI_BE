package prgrms.project.stuti.domain.studygroup.service.dto;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lombok.Builder;
import prgrms.project.stuti.domain.member.model.Mbti;
import prgrms.project.stuti.domain.studygroup.model.Region;
import prgrms.project.stuti.domain.studygroup.model.Topic;

public record StudyGroupCreateDto(
	Long memberId,
	MultipartFile imageFile,
	String title,
	Topic topic,
	boolean isOnline,
	Region region,
	List<Mbti> preferredMbtis,
	int numberOfRecruits,
	LocalDateTime startDateTime,
	LocalDateTime endDateTime,
	String description
) {

	@Builder
	public StudyGroupCreateDto {
	}
}
