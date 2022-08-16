package prgrms.project.stuti.domain.studygroup.repository.dto;

import prgrms.project.stuti.domain.member.model.Career;
import prgrms.project.stuti.domain.member.model.Field;
import prgrms.project.stuti.domain.member.model.Mbti;
import prgrms.project.stuti.domain.studygroup.model.StudyGroupMemberRole;

public record StudyGroupMemberQueryDto(
	Long studyGroupMemberId,
	String profileImageUrl,
	String nickname,
	Field field,
	Career career,
	Mbti mbti,
	StudyGroupMemberRole studyGroupMemberRole
) {

}
