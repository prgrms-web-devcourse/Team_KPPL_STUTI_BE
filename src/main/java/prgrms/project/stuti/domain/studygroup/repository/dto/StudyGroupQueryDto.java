package prgrms.project.stuti.domain.studygroup.repository.dto;

import java.time.LocalDateTime;
import java.util.List;

import prgrms.project.stuti.domain.member.model.Career;
import prgrms.project.stuti.domain.member.model.Field;
import prgrms.project.stuti.domain.member.model.Mbti;
import prgrms.project.stuti.domain.studygroup.model.Region;
import prgrms.project.stuti.domain.studygroup.model.StudyGroup;
import prgrms.project.stuti.domain.studygroup.model.StudyGroupMemberRole;
import prgrms.project.stuti.domain.studygroup.model.Topic;

public record StudyGroupQueryDto() {

	public static record StudyGroupDetailDto(
		Long studyGroupId,
		Topic topic,
		String title,
		String imageUrl,
		Long memberId,
		String profileImageUrl,
		String nickname,
		Field field,
		Career career,
		Mbti mbti,
		Mbti preferredMBTI,
		boolean isOnline,
		Region region,
		LocalDateTime startDateTime,
		LocalDateTime endDateTime,
		int numberOfMembers,
		int numberOfRecruits,
		String description
	) {

	}

	public static record StudyGroupsDto(
		List<StudyGroupDto> studyGroupDtos,
		boolean hasNext
	) {

	}

	public static record StudyGroupDto(
		StudyGroup studyGroup,
		Long memberId
	) {

	}

	public static record StudyGroupMemberDto(
		Long studyGroupMemberId,
		String profileImageUrl,
		String nickname,
		Field field,
		Career career,
		Mbti mbti,
		StudyGroupMemberRole studyGroupMemberRole
	) {

	}
}
