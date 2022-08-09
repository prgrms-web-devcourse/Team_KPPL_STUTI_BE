package prgrms.project.stuti.domain.studygroup.controller;

import java.util.Set;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import prgrms.project.stuti.domain.member.model.Mbti;
import prgrms.project.stuti.domain.studygroup.controller.dto.StudyGroupRequest;
import prgrms.project.stuti.domain.studygroup.model.Region;
import prgrms.project.stuti.domain.studygroup.service.dto.StudyGroupDto;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StudyGroupMapper {

	public static StudyGroupDto.CreateDto toStudyGroupCreateDto(
		Long memberId, StudyGroupRequest.CreateRequest createRequest
	) {
		return StudyGroupDto.CreateDto
			.builder()
			.memberId(memberId)
			.title(createRequest.title())
			.topic(createRequest.topic())
			.isOnline(createRequest.isOnline())
			.region(createRequest.isOnline() ? Region.ONLINE : createRequest.region())
			.numberOfRecruits(createRequest.numberOfRecruits())
			.startDateTime(createRequest.startDateTime())
			.endDateTime(createRequest.endDateTime())
			.preferredMBTIs(
				createRequest.preferredMBTIs().isEmpty() ? Set.of(Mbti.NONE) : createRequest.preferredMBTIs())
			.imageFile(createRequest.imageFile())
			.description(createRequest.description())
			.build();
	}

	public static StudyGroupDto.FindCondition toStudyGroupFindConditionDto(
		Long memberId, Long size, StudyGroupRequest.FindCondition condition
	) {
		return StudyGroupDto.FindCondition
			.builder()
			.mbti(condition.mbti())
			.topic(condition.topic())
			.region(condition.region())
			.studyGroupMemberRole(condition.studyGroupMemberRole())
			.memberId(memberId)
			.lastStudyGroupId(condition.lastStudyGroupId())
			.size(size)
			.build();
	}

	public static StudyGroupDto.FindCondition toStudyGroupFindConditionDto(
		Long size, StudyGroupRequest.FindCondition condition
	) {
		return StudyGroupDto.FindCondition
			.builder()
			.mbti(condition.mbti())
			.topic(condition.topic())
			.region(condition.region())
			.studyGroupMemberRole(condition.studyGroupMemberRole())
			.lastStudyGroupId(condition.lastStudyGroupId())
			.size(size)
			.build();
	}

	public static StudyGroupDto.ReadDto toStudyGroupReadDto(Long studyGroupId) {
		return new StudyGroupDto.ReadDto(studyGroupId);
	}

	public static StudyGroupDto.UpdateDto toStudyGroupUpdateDto(
		Long memberId, Long studyGroupId, StudyGroupRequest.UpdateRequest updateRequest
	) {
		return StudyGroupDto.UpdateDto
			.builder()
			.memberId(memberId)
			.studyGroupId(studyGroupId)
			.title(updateRequest.title())
			.imageFile(updateRequest.imageFile())
			.description(updateRequest.description())
			.build();
	}

	public static StudyGroupDto.DeleteDto toStudyGroupDeleteDto(Long memberId, Long studyGroupId) {
		return new StudyGroupDto.DeleteDto(memberId, studyGroupId);
	}
}
