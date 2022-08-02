package prgrms.project.stuti.domain.studygroup.repository.studymember;

import static prgrms.project.stuti.domain.member.model.QMember.*;
import static prgrms.project.stuti.domain.studygroup.model.QStudyGroup.*;
import static prgrms.project.stuti.domain.studygroup.model.QStudyMember.*;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import prgrms.project.stuti.domain.studygroup.model.StudyMemberRole;

@RequiredArgsConstructor
public class CustomStudyMemberRepositoryImpl implements CustomStudyMemberRepository {

	private final JPAQueryFactory jpaQueryFactory;

	@Override
	public boolean isLeader(Long memberId, Long studyGroupId) {
		Integer result = jpaQueryFactory
			.selectOne()
			.from(studyMember)
			.join(studyMember.member, member)
			.join(studyMember.studyGroup, studyGroup)
			.where(isLeader(), isEqualIdAndNotDeletedMember(memberId), isEqualIdAndNotDeletedStudyGroup(studyGroupId))
			.fetchFirst();

		return result != null;
	}

	private BooleanExpression isLeader() {
		return studyMember.studyMemberRole.eq(StudyMemberRole.LEADER);
	}

	private BooleanExpression isEqualIdAndNotDeletedMember(Long memberId) {
		return member.id.eq(memberId).and(isNotDeletedMember());
	}

	private BooleanExpression isNotDeletedMember() {
		return member.isDeleted.isFalse();
	}

	private BooleanExpression isEqualIdAndNotDeletedStudyGroup(Long studyGroupId) {
		return studyGroup.id.eq(studyGroupId).and(studyGroup.isDeleted.isFalse());
	}
}
