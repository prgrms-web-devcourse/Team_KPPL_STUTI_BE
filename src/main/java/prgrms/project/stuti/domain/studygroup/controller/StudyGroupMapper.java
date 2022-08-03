package prgrms.project.stuti.domain.studygroup.controller;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import prgrms.project.stuti.domain.studygroup.controller.dto.QuestionCreateRequest;
import prgrms.project.stuti.domain.studygroup.controller.dto.QuestionUpdateRequest;
import prgrms.project.stuti.domain.studygroup.controller.dto.StudyGroupCreateRequest;
import prgrms.project.stuti.domain.studygroup.controller.dto.StudyGroupUpdateRequest;
import prgrms.project.stuti.domain.studygroup.model.Region;
import prgrms.project.stuti.domain.studygroup.service.dto.QuestionCreateDto;
import prgrms.project.stuti.domain.studygroup.service.dto.QuestionUpdateDto;
import prgrms.project.stuti.domain.studygroup.service.dto.StudyGroupCreateDto;
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

	public static QuestionCreateDto toQuestionCreateDto(Long memberId, Long studyGroupId,
		QuestionCreateRequest createRequest) {
		return QuestionCreateDto
			.builder()
			.memberId(memberId)
			.studyGroupId(studyGroupId)
			.parentId(createRequest.parentId())
			.content(createRequest.content())
			.build();
	}

	public static QuestionUpdateDto toQuestionUpdateDto(Long memberId, Long studyGroupId,
		Long questionId, QuestionUpdateRequest updateRequest) {
		return new QuestionUpdateDto(memberId, studyGroupId, questionId, updateRequest.content());
	}
}
