package prgrms.project.stuti.domain.studygroup.repository.studymember;

import static prgrms.project.stuti.domain.studygroup.model.QStudyMember.*;

import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import prgrms.project.stuti.domain.member.model.QMember;
import prgrms.project.stuti.domain.studygroup.model.QStudyGroup;
import prgrms.project.stuti.domain.studygroup.model.QStudyMember;
import prgrms.project.stuti.domain.studygroup.model.StudyMemberRole;

@RequiredArgsConstructor
public class CustomStudyMemberRepositoryImpl implements CustomStudyMemberRepository {

	private final JPAQueryFactory jpaQueryFactory;

	@Override
	public boolean isLeader(Long memberId, Long studyGroupId) {
		QStudyGroup studyGroup = studyMember.studyGroup;
		QMember member = studyMember.member;

		Integer result = jpaQueryFactory
			.selectOne()
			.from(QStudyMember.studyMember)
			.where(QStudyMember.studyMember.studyMemberRole.eq(StudyMemberRole.LEADER), member.id.eq(memberId),
				studyGroup.id.eq(studyGroupId), studyGroup.isDeleted.isFalse())
			.fetchFirst();

		return result != null;
	}
}
