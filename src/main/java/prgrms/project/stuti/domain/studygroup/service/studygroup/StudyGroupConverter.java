package prgrms.project.stuti.domain.studygroup.service.studygroup;

import java.util.List;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import prgrms.project.stuti.domain.studygroup.model.StudyGroup;
import prgrms.project.stuti.domain.studygroup.model.StudyPeriod;
import prgrms.project.stuti.domain.studygroup.service.dto.LeaderResponse;
import prgrms.project.stuti.domain.studygroup.service.dto.StudyGroupCreateDto;
import prgrms.project.stuti.domain.studygroup.service.dto.StudyGroupDetailDto;
import prgrms.project.stuti.domain.studygroup.service.dto.StudyGroupIdResponse;
import prgrms.project.stuti.domain.studygroup.service.dto.StudyGroupDetailResponse;

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
			.description(createDto.description())
			.build();
	}

	public static StudyGroupIdResponse toStudyGroupIdResponse(Long studyGroupId) {
		return new StudyGroupIdResponse(studyGroupId);
	}

	public static StudyGroupDetailResponse toStudyGroupResponse(List<StudyGroupDetailDto> detailDtos) {
		StudyGroupDetailDto detailDto = detailDtos.get(0);

		return StudyGroupDetailResponse
			.builder()
			.studyGroupId(detailDto.studyGroupId())
			.topic(detailDto.topic().getValue())
			.title(detailDto.title())
			.imageUrl(detailDto.imageUrl())
			.leader(toLeaderResponse(detailDto))
			.preferredMBTIs(toPreferredMBTIs(detailDtos))
			.isOnline(detailDto.isOnline())
			.region(detailDto.region().getValue())
			.startDateTime(detailDto.startDateTime())
			.endDateTime(detailDto.endDateTime())
			.numberOfMembers(detailDto.numberOfMembers())
			.numberOfRecruits(detailDto.numberOfRecruits())
			.description(detailDto.description())
			.build();
	}

	private static LeaderResponse toLeaderResponse(StudyGroupDetailDto detailDto) {
		return LeaderResponse
			.builder()
			.memberId(detailDto.memberId())
			.profileImageUrl(detailDto.profileImageUrl())
			.nickname(detailDto.nickname())
			.field(detailDto.field().getFieldValue())
			.career(detailDto.career().getCareerValue())
			.mbti(String.valueOf(detailDto.mbti()))
			.build();
	}

	private static List<String> toPreferredMBTIs(List<StudyGroupDetailDto> detailDtos) {
		return detailDtos.stream().map(dto -> String.valueOf(dto.preferredMBTI())).toList();
	}
}
