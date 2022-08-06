package prgrms.project.stuti.domain.studygroup.controller;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import prgrms.project.stuti.domain.studygroup.controller.dto.StudyGroupQuestionRequest;
import prgrms.project.stuti.domain.studygroup.service.dto.StudyGroupQuestionDto;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StudyGroupQuestionMapper {
	public static StudyGroupQuestionDto.CreateDto toStudyGroupQuestionCreateDto(Long memberId, Long studyGroupId,
		StudyGroupQuestionRequest.CreateRequest createRequest) {
		return StudyGroupQuestionDto.CreateDto
			.builder()
			.memberId(memberId)
			.studyGroupId(studyGroupId)
			.parentId(createRequest.parentId())
			.contents(createRequest.contents())
			.build();
	}

	public static StudyGroupQuestionDto.PageDto toStudyGroupQuestionPageDto(Long studyGroupId, Long size,
		Long lastStudyGroupQuestionId) {
		return new StudyGroupQuestionDto.PageDto(studyGroupId, size, lastStudyGroupQuestionId);
	}

	public static StudyGroupQuestionDto.UpdateDto toStudyGroupQuestionUpdateDto(Long memberId, Long studyGroupId,
		Long studyGroupQuestionId, StudyGroupQuestionRequest.UpdateRequest updateRequest) {
		return StudyGroupQuestionDto.UpdateDto
			.builder()
			.memberId(memberId)
			.studyGroupId(studyGroupId)
			.studyGroupQuestionId(studyGroupQuestionId)
			.contents(updateRequest.contents())
			.build();
	}

	public static StudyGroupQuestionDto.DeleteDto toStudyGroupQuestionDeleteDto(Long memberId, Long studyGroupId,
		Long studyGroupQuestionId) {
		return new StudyGroupQuestionDto.DeleteDto(memberId, studyGroupId, studyGroupQuestionId);
	}
}
