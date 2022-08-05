package prgrms.project.stuti.domain.studygroup.service.studygroup;

import java.util.HashSet;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import prgrms.project.stuti.domain.member.model.Member;
import prgrms.project.stuti.domain.studygroup.model.StudyGroup;
import prgrms.project.stuti.domain.studygroup.model.StudyMember;
import prgrms.project.stuti.domain.studygroup.model.StudyPeriod;
import prgrms.project.stuti.domain.studygroup.service.dto.StudyGroupCreateDto;
import prgrms.project.stuti.domain.studygroup.service.response.StudyGroupDetailResponse;
import prgrms.project.stuti.domain.studygroup.service.response.StudyGroupIdResponse;
import prgrms.project.stuti.domain.studygroup.service.response.StudyMemberResponse;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StudyGroupConverter {

	public static StudyGroup toStudyGroup(StudyGroupCreateDto createDto, String imageUrl, String thumbnailUrl) {
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

	public static StudyGroupDetailResponse toStudyGroupDetailResponse(StudyMember studyGroupDetail) {
		StudyGroup studyGroup = studyGroupDetail.getStudyGroup();
		StudyPeriod studyPeriod = studyGroup.getStudyPeriod();
		Member member = studyGroupDetail.getMember();

		return StudyGroupDetailResponse
			.builder()
			.studyGroupId(studyGroup.getId())
			.topic(studyGroup.getTopic().getValue())
			.title(studyGroup.getTitle())
			.imageUrl(studyGroup.getImageUrl())
			.leader(toStudyMemberResponse(member))
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

	private static StudyMemberResponse toStudyMemberResponse(Member member) {
		return StudyMemberResponse
			.builder()
			.memberId(member.getId())
			.profileImageUrl(member.getProfileImageUrl())
			.nickname(member.getNickName())
			.field(member.getField().getFieldValue())
			.career(member.getCareer().getCareerValue())
			.mbti(member.getMbti())
			.build();
	}
}
