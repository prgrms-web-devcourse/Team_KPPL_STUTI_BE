package prgrms.project.stuti.domain.studygroup.service.dto;

import lombok.Builder;
import prgrms.project.stuti.domain.member.model.Mbti;
import prgrms.project.stuti.domain.studygroup.model.Region;
import prgrms.project.stuti.domain.studygroup.model.StudyGroupMemberRole;
import prgrms.project.stuti.domain.studygroup.model.Topic;

public record StudyGroupFindConditionDto(
	Mbti mbti,
	Topic topic,
	Region region,
	StudyGroupMemberRole studyGroupMemberRole,
	Long memberId,
	Long lastStudyGroupId,
	Long size
) {

	@Builder
	public StudyGroupFindConditionDto {
	}
}
