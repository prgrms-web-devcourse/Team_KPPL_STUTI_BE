package prgrms.project.stuti.domain.studygroup.service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import prgrms.project.stuti.domain.member.model.Member;
import prgrms.project.stuti.domain.studygroup.model.StudyGroup;
import prgrms.project.stuti.domain.studygroup.model.StudyGroupMember;
import prgrms.project.stuti.domain.studygroup.model.StudyGroupMemberRole;
import prgrms.project.stuti.domain.studygroup.service.response.StudyGroupMemberIdResponse;
import prgrms.project.stuti.domain.studygroup.service.response.StudyGroupMembersResponse;
import prgrms.project.stuti.domain.studygroup.service.response.StudyGroupMembersResponse.StudyGroupMemberResponse;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StudyGroupMemberConverter {

	public static StudyGroupMemberIdResponse toStudyGroupMemberIdResponse(Long studyGroupMemberId) {
		return new StudyGroupMemberIdResponse(studyGroupMemberId);
	}

	public static StudyGroupMembersResponse toStudyGroupMembersResponse(List<StudyGroupMember> studyGroupMembers) {
		Map<StudyGroupMemberRole, List<StudyGroupMember>> studyGroupMemberMap = studyGroupMembers
			.stream()
			.collect(Collectors.groupingBy(StudyGroupMember::getStudyGroupMemberRole));

		StudyGroupMember studyLeader = studyGroupMemberMap.get(StudyGroupMemberRole.STUDY_LEADER).get(0);
		List<StudyGroupMember> studyMembers =
			studyGroupMemberMap.getOrDefault(StudyGroupMemberRole.STUDY_MEMBER, Collections.emptyList());
		List<StudyGroupMember> studyApplicants =
			studyGroupMemberMap.getOrDefault(StudyGroupMemberRole.STUDY_APPLICANT, Collections.emptyList());
		StudyGroup studyGroup = studyLeader.getStudyGroup();

		return StudyGroupMembersResponse
			.builder()
			.studyGroupId(studyGroup.getId())
			.topic(studyGroup.getTopic().getValue())
			.title(studyGroup.getTitle())
			.numberOfMembers(studyGroup.getNumberOfMembers())
			.numberOfRecruits(studyGroup.getNumberOfRecruits())
			.studyMembers(toStudyGroupMemberResponse(studyMembers))
			.numberOfApplicants(studyGroup.getNumberOfApplicants())
			.studyApplicants(toStudyGroupMemberResponse(studyApplicants))
			.build();
	}

	private static List<StudyGroupMemberResponse> toStudyGroupMemberResponse(List<StudyGroupMember> studyGroupMembers) {
		return studyGroupMembers.isEmpty()
			? Collections.emptyList()
			: studyGroupMembers.stream().map(studyGroupMember -> {
			Member member = studyGroupMember.getMember();

			return StudyGroupMemberResponse
				.builder()
				.studyGroupMemberId(studyGroupMember.getId())
				.profileImageUrl(member.getProfileImageUrl())
				.nickname(member.getNickName())
				.field(member.getField().getFieldValue())
				.career(member.getCareer().getCareerValue())
				.mbti(member.getMbti())
				.studyGroupMemberRole(studyGroupMember.getStudyGroupMemberRole().getValue())
				.build();
		}).toList();
	}
}
