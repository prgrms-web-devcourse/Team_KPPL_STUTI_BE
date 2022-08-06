package prgrms.project.stuti.domain.studygroup.controller;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import prgrms.project.stuti.domain.studygroup.service.dto.StudyGroupMemberDto;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StudyGroupMemberMapper {

	public static StudyGroupMemberDto.CreateDto toStudyGroupMemberCreateDto(Long memberId, Long studyGroupId) {
		return new StudyGroupMemberDto.CreateDto(memberId, studyGroupId);
	}

	public static StudyGroupMemberDto.ReadDto toStudyGroupMemberReadDto(Long memberId, Long studyGroupId) {
		return new StudyGroupMemberDto.ReadDto(memberId, studyGroupId);
	}

	public static StudyGroupMemberDto.UpdateDto toStudyGroupMemberUpdateDto(Long memberId, Long studyGroupId,
		Long studyGroupMemberId) {
		return new StudyGroupMemberDto.UpdateDto(memberId, studyGroupId, studyGroupMemberId);
	}

	public static StudyGroupMemberDto.DeleteDto toStudyGroupMemberDeleteDto(Long memberId, Long studyGroupId,
		Long studyGroupMemberId) {
		return new StudyGroupMemberDto.DeleteDto(memberId, studyGroupId, studyGroupMemberId);
	}
}
