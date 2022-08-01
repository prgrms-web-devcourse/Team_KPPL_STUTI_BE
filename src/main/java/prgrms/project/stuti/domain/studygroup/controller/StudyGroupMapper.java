package prgrms.project.stuti.domain.studygroup.controller;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import prgrms.project.stuti.domain.studygroup.controller.dto.StudyGroupCreateRequest;
import prgrms.project.stuti.domain.studygroup.model.Region;
import prgrms.project.stuti.domain.studygroup.service.dto.StudyGroupCreateDto;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StudyGroupMapper {

	public static StudyGroupCreateDto toStudyGroupCreateDto(Long memberId, StudyGroupCreateRequest createRequest) {
		return StudyGroupCreateDto
			.builder()
			.memberId(memberId)
			.imageFile(createRequest.imageFile())
			.title(createRequest.title())
			.topic(createRequest.topic())
			.isOnline(createRequest.isOnline())
			.region(createRequest.isOnline() ? Region.ONLINE : createRequest.region())
			.preferredMBTIs(createRequest.preferredMBTIs())
			.numberOfRecruits(createRequest.numberOfRecruits())
			.startDateTime(createRequest.startDateTime())
			.endDateTime(createRequest.endDateTime())
			.description(createRequest.description())
			.build();
	}
}
