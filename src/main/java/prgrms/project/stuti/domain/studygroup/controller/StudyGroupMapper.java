package prgrms.project.stuti.domain.studygroup.controller;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import prgrms.project.stuti.domain.studygroup.controller.dto.StudyGroupCreateRequest;
import prgrms.project.stuti.domain.studygroup.controller.dto.StudyGroupQuestionCreateRequest;
import prgrms.project.stuti.domain.studygroup.controller.dto.StudyGroupQuestionUpdateRequest;
import prgrms.project.stuti.domain.studygroup.controller.dto.StudyGroupUpdateRequest;
import prgrms.project.stuti.domain.studygroup.model.Region;
import prgrms.project.stuti.domain.studygroup.service.dto.StudyGroupCreateDto;
import prgrms.project.stuti.domain.studygroup.service.dto.StudyGroupQuestionCreateDto;
import prgrms.project.stuti.domain.studygroup.service.dto.StudyGroupQuestionUpdateDto;
import prgrms.project.stuti.domain.studygroup.service.dto.StudyGroupUpdateDto;

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

	public static StudyGroupUpdateDto toStudyGroupUpdateDto(Long memberId, Long studyGroupId,
		StudyGroupUpdateRequest updateRequest) {
		return StudyGroupUpdateDto
			.builder()
			.memberId(memberId)
			.studyGroupId(studyGroupId)
			.title(updateRequest.title())
			.imageFile(updateRequest.imageFile())
			.description(updateRequest.description())
			.build();
	}

	public static StudyGroupQuestionCreateDto toStudyGroupQuestionCreateDto(Long memberId, Long studyGroupId,
		StudyGroupQuestionCreateRequest createRequest) {
		return StudyGroupQuestionCreateDto
			.builder()
			.memberId(memberId)
			.studyGroupId(studyGroupId)
			.parentId(createRequest.parentId())
			.contents(createRequest.contents())
			.build();
	}

	public static StudyGroupQuestionUpdateDto toStudyGroupQuestionUpdateDto(Long memberId, Long studyGroupId,
		Long studyGroupQuestionId, StudyGroupQuestionUpdateRequest updateRequest) {
		return new StudyGroupQuestionUpdateDto(memberId, studyGroupId, studyGroupQuestionId, updateRequest.contents());
	}
}
