package prgrms.project.stuti.domain.studygroup.service;

import java.util.HashSet;
import java.util.List;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import prgrms.project.stuti.domain.member.model.Member;
import prgrms.project.stuti.domain.studygroup.model.StudyGroup;
import prgrms.project.stuti.domain.studygroup.model.StudyGroupMember;
import prgrms.project.stuti.domain.studygroup.model.StudyPeriod;
import prgrms.project.stuti.domain.studygroup.service.dto.StudyGroupDto;
import prgrms.project.stuti.domain.studygroup.service.response.StudyGroupIdResponse;
import prgrms.project.stuti.domain.studygroup.service.response.StudyGroupResponse;
import prgrms.project.stuti.domain.studygroup.service.response.StudyGroupsResponse;
import prgrms.project.stuti.global.page.CursorPageResponse;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StudyGroupConverter {

	public static StudyGroup toStudyGroup(StudyGroupDto.CreateDto createDto, String imageUrl, String thumbnailUrl) {
		return StudyGroup
			.builder()
			.imageUrl(imageUrl)
			.thumbnailUrl(thumbnailUrl)
			.title(createDto.title())
			.topic(createDto.topic())
			.isOnline(createDto.isOnline())
			.region(createDto.region())
			.numberOfRecruits(createDto.numberOfRecruits())
			.studyPeriod(new StudyPeriod(createDto.startDateTime(), createDto.endDateTime()))
			.preferredMBTIs(new HashSet<>(createDto.preferredMBTIs()))
			.description(createDto.description())
			.build();
	}

	public static StudyGroupIdResponse toStudyGroupIdResponse(Long studyGroupId) {
		return new StudyGroupIdResponse(studyGroupId);
	}

	public static StudyGroupResponse toStudyGroupResponse(StudyGroupMember studyGroupDetail) {
		StudyGroup studyGroup = studyGroupDetail.getStudyGroup();
		StudyPeriod studyPeriod = studyGroup.getStudyPeriod();
		Member member = studyGroupDetail.getMember();

		return StudyGroupResponse
			.builder()
			.studyGroupId(studyGroup.getId())
			.topic(studyGroup.getTopic().getValue())
			.title(studyGroup.getTitle())
			.imageUrl(studyGroup.getImageUrl())
			.leader(toStudyGroupLeaderResponse(member))
			.preferredMBTIs(studyGroup.getPreferredMBTIs())
			.isOnline(studyGroup.isOnline())
			.region(studyGroup.getRegion().getValue())
			.startDateTime(studyPeriod.getStartDateTime())
			.endDateTime(studyPeriod.getEndDateTime())
			.numberOfMembers(studyGroup.getNumberOfMembers())
			.numberOfRecruits(studyGroup.getNumberOfRecruits())
			.description(studyGroup.getDescription())
			.build();
	}

	public static CursorPageResponse<StudyGroupsResponse> toStudyGroupsCursorPageResponse(
		List<StudyGroupMember> studyGroupMembers, boolean hasNext) {
		return new CursorPageResponse<>(toStudyGroupResponse(studyGroupMembers), hasNext);
	}

	private static StudyGroupResponse.StudyGroupLeader toStudyGroupLeaderResponse(Member member) {
		return StudyGroupResponse.StudyGroupLeader
			.builder()
			.memberId(member.getId())
			.profileImageUrl(member.getProfileImageUrl())
			.nickname(member.getNickName())
			.field(member.getField().getFieldValue())
			.career(member.getCareer().getCareerValue())
			.mbti(member.getMbti())
			.build();
	}

	private static List<StudyGroupsResponse> toStudyGroupResponse(List<StudyGroupMember> studyGroupMembers) {
		return studyGroupMembers
			.stream()
			.map(studyGroupMember -> {
				Member member = studyGroupMember.getMember();
				StudyGroup studyGroup = studyGroupMember.getStudyGroup();
				StudyPeriod studyPeriod = studyGroup.getStudyPeriod();

				return StudyGroupsResponse
					.builder()
					.studyGroupId(studyGroup.getId())
					.memberId(member.getId())
					.thumbnailUrl(studyGroup.getThumbnailUrl())
					.topic(studyGroup.getTopic().getValue())
					.title(studyGroup.getTitle())
					.preferredMBTIs(studyGroup.getPreferredMBTIs())
					.region(studyGroup.getRegion().getValue())
					.startDateTime(studyPeriod.getStartDateTime())
					.endDateTime(studyPeriod.getEndDateTime())
					.numberOfMembers(studyGroup.getNumberOfMembers())
					.numberOfRecruits(studyGroup.getNumberOfRecruits())
					.build();
			}).toList();
	}
}
