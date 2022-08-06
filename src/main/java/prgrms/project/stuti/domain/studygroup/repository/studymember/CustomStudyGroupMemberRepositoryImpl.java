package prgrms.project.stuti.domain.studygroup.repository.studymember;

import static prgrms.project.stuti.domain.member.model.QMember.*;
import static prgrms.project.stuti.domain.studygroup.model.QStudyGroup.*;
import static prgrms.project.stuti.domain.studygroup.model.QStudyGroupMember.*;
import static prgrms.project.stuti.domain.studygroup.repository.CommonStudyGroupBooleanExpression.*;

import java.util.List;
import java.util.Optional;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import prgrms.project.stuti.domain.studygroup.model.StudyGroupMember;
import prgrms.project.stuti.domain.studygroup.model.StudyGroupMemberRole;

@RequiredArgsConstructor
public class CustomStudyGroupMemberRepositoryImpl implements CustomStudyGroupMemberRepository {

	private final JPAQueryFactory jpaQueryFactory;

	@Override
	public boolean isStudyLeader(Long memberId, Long studyGroupId) {
		Integer result = jpaQueryFactory
			.selectOne()
			.from(studyGroupMember)
			.join(studyGroupMember.member, member)
			.join(studyGroupMember.studyGroup, studyGroup)
			.where(hasStudyGroupMemberRole(StudyGroupMemberRole.STUDY_LEADER), equalMember(memberId),
				equalStudyGroup(studyGroupId))
			.fetchFirst();

		return result != null;
	}

	public Optional<StudyGroupMember> findStudyGroupMemberById(Long studyGroupMemberId) {
		return Optional.ofNullable(
			jpaQueryFactory
				.selectFrom(studyGroupMember)
				.join(studyGroupMember.member, member)
				.join(studyGroupMember.studyGroup, studyGroup)
				.where(equalStudyGroupMemberId(studyGroupMemberId), notDeletedMember(), notDeletedStudyGroup())
				.fetchFirst()
		);
	}

	@Override
	public List<StudyGroupMember> findStudyGroupMembers(Long memberId, Long studyGroupId) {
		return jpaQueryFactory
			.selectFrom(studyGroupMember)
			.join(studyGroupMember.member, member).fetchJoin()
			.join(studyGroupMember.studyGroup, studyGroup).fetchJoin()
			.where(notDeletedMember(), equalStudyGroup(studyGroupId))
			.orderBy(studyGroupMember.createdAt.asc())
			.fetch();
	}

	private BooleanExpression equalStudyGroupMemberId(Long studyGroupMemberId) {
		return studyGroupMemberId == null ? null : studyGroupMember.id.eq(studyGroupMemberId);
	}
}
