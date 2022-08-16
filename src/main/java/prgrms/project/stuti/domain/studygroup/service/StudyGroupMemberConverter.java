package prgrms.project.stuti.domain.studygroup.service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import prgrms.project.stuti.domain.studygroup.model.StudyGroup;
import prgrms.project.stuti.domain.studygroup.model.StudyGroupMemberRole;
import prgrms.project.stuti.domain.studygroup.repository.dto.StudyGroupMemberQueryDto;
import prgrms.project.stuti.domain.studygroup.service.response.StudyGroupMemberIdResponse;
import prgrms.project.stuti.domain.studygroup.service.response.StudyGroupMembersResponse;
import prgrms.project.stuti.domain.studygroup.service.response.StudyGroupMembersResponse.StudyGroupMemberResponse;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StudyGroupMemberConverter {

	public static StudyGroupMemberIdResponse toStudyGroupMemberIdResponse(Long studyGroupMemberId) {
		return new StudyGroupMemberIdResponse(studyGroupMemberId);
	}

	public static StudyGroupMembersResponse toStudyGroupMembersResponse(
		StudyGroup studyGroup, List<StudyGroupMemberQueryDto> studyGroupMemberQueryDtos
	) {

		Map<Boolean, List<StudyGroupMemberQueryDto>> studyGroupMembersDtoMap = studyGroupMemberQueryDtos
			.stream()
			.collect(Collectors.partitioningBy(
				dto -> dto.studyGroupMemberRole().equals(StudyGroupMemberRole.STUDY_APPLICANT)));

		return StudyGroupMembersResponse
			.builder()
			.studyGroupId(studyGroup.getId())
			.topic(studyGroup.getTopic().getValue())
			.title(studyGroup.getTitle())
			.numberOfMembers(studyGroup.getNumberOfMembers())
			.numberOfRecruits(studyGroup.getNumberOfRecruits())
			.studyMembers(toStudyGroupMemberResponse(studyGroupMembersDtoMap.get(false)))
			.numberOfApplicants(studyGroup.getNumberOfApplicants())
			.studyApplicants(toStudyGroupMemberResponse(studyGroupMembersDtoMap.get(true)))
			.build();
	}

	private static List<StudyGroupMemberResponse> toStudyGroupMemberResponse(
		List<StudyGroupMemberQueryDto> studyGroupMemberDtos
	) {
		return studyGroupMemberDtos.isEmpty()
			? Collections.emptyList()
			: studyGroupMemberDtos
			.stream()
			.map(studyGroupMemberDto -> StudyGroupMemberResponse
				.builder()
				.studyGroupMemberId(studyGroupMemberDto.studyGroupMemberId())
				.profileImageUrl(studyGroupMemberDto.profileImageUrl())
				.nickname(studyGroupMemberDto.nickname())
				.field(studyGroupMemberDto.field().getFieldValue())
				.career(studyGroupMemberDto.career().getCareerValue())
				.mbti(studyGroupMemberDto.mbti())
				.studyGroupMemberRole(studyGroupMemberDto.studyGroupMemberRole().getValue())
				.build())
			.toList();
	}
}
