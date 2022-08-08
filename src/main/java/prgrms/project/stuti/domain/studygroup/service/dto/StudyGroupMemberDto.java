package prgrms.project.stuti.domain.studygroup.service.dto;

public record StudyGroupMemberDto() {

	public static record CreateDto(Long memberId, Long studyGroupId) { }

	public static record ReadDto(Long memberId, Long studyGroupId) { }

	public static record UpdateDto(
		Long memberId,
		Long studyGroupId,
		Long studyGroupMemberId
	) {

	}

	public static record DeleteDto(
		Long memberId,
		Long studyGroupId,
		Long studyGroupMemberId
	) {

	}
}
