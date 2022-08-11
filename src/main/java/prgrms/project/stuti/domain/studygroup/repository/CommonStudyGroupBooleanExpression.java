package prgrms.project.stuti.domain.studygroup.repository;

import static prgrms.project.stuti.domain.member.model.QMember.*;
import static prgrms.project.stuti.domain.studygroup.model.QStudyGroup.*;
import static prgrms.project.stuti.domain.studygroup.model.QStudyGroupMember.*;

import com.querydsl.core.types.dsl.BooleanExpression;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import prgrms.project.stuti.domain.studygroup.model.StudyGroupMemberRole;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommonStudyGroupBooleanExpression {

	public static BooleanExpression eqStudyGroupMemberRole(StudyGroupMemberRole studyGroupMemberRole) {
		return studyGroupMemberRole == null ? null : studyGroupMember.studyGroupMemberRole.eq(studyGroupMemberRole);
	}

	public static BooleanExpression eqAndNotDeletedMember(Long memberId) {
		return eqMemberId(memberId).and(notDeletedMember());
	}

	public static BooleanExpression eqAndNotDeletedStudyGroup(Long studyGroupId) {
		return eqStudyGroupId(studyGroupId).and(notDeletedStudyGroup());
	}

	public static BooleanExpression notDeletedStudyGroup() {
		return studyGroup.id.isNotNull().and(studyGroup.isDeleted.isFalse());
	}

	public static BooleanExpression notDeletedMember() {
		return member.id.isNotNull().and(member.isDeleted.isFalse());
	}

	private static BooleanExpression eqStudyGroupId(Long studyGroupId) {
		return studyGroup.id.eq(studyGroupId);
	}

	public static BooleanExpression eqMemberId(Long memberId) {
		return member.id.eq(memberId);
	}
}
