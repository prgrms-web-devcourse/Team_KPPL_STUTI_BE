package prgrms.project.stuti.domain.studygroup.repository;

import static prgrms.project.stuti.domain.member.model.QMember.*;
import static prgrms.project.stuti.domain.studygroup.model.QStudyGroup.*;
import static prgrms.project.stuti.domain.studygroup.model.QStudyMember.*;

import com.querydsl.core.types.dsl.BooleanExpression;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import prgrms.project.stuti.domain.studygroup.model.StudyMemberRole;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommonStudyGroupBooleanExpression {

	public static BooleanExpression hasStudyMemberRole(StudyMemberRole studyMemberRole) {
		return studyMember.studyMemberRole.eq(studyMemberRole);
	}

	public static BooleanExpression isNotDeletedMember() {
		return member.isDeleted.isFalse();
	}

	public static BooleanExpression isEqualIdAndNotDeletedMember(Long memberId) {
		return isEqualIdMember(memberId).and(isNotDeletedMember());
	}

	public static BooleanExpression isEqualIdAndNotDeletedStudyGroup(Long studyGroupId) {
		return isEqualIdStudyGroup(studyGroupId).and(isNotDeletedStudyGroup());
	}

	private static BooleanExpression isNotDeletedStudyGroup() {
		return studyGroup.isDeleted.isFalse();
	}

	private static BooleanExpression isEqualIdStudyGroup(Long studyGroupId) {
		return studyGroup.id.eq(studyGroupId);
	}

	private static BooleanExpression isEqualIdMember(Long memberId) {
		return member.id.eq(memberId);
	}
}
