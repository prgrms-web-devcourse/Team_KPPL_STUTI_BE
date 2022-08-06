package prgrms.project.stuti.domain.studygroup.controller.dto;

import prgrms.project.stuti.domain.member.model.Mbti;
import prgrms.project.stuti.domain.studygroup.model.Region;
import prgrms.project.stuti.domain.studygroup.model.StudyGroupMemberRole;
import prgrms.project.stuti.domain.studygroup.model.Topic;

public record StudyGroupFindCondition(
	Mbti mbti,
	Topic topic,
	Region region,
	StudyGroupMemberRole studyGroupMemberRole,
	Long lastStudyGroupId
) {
}
