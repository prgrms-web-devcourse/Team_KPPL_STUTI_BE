package prgrms.project.stuti.domain.studygroup.repository.studymember;

import static com.querydsl.core.group.GroupBy.*;
import static prgrms.project.stuti.domain.member.model.QMember.*;
import static prgrms.project.stuti.domain.studygroup.model.QStudyGroup.*;
import static prgrms.project.stuti.domain.studygroup.model.QStudyGroupMember.*;
import static prgrms.project.stuti.domain.studygroup.repository.CommonStudyGroupBooleanExpression.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import prgrms.project.stuti.domain.studygroup.model.StudyGroup;
import prgrms.project.stuti.domain.studygroup.model.StudyGroupMember;
import prgrms.project.stuti.domain.studygroup.model.StudyGroupMemberRole;
import prgrms.project.stuti.domain.studygroup.repository.dto.StudyGroupQueryDto;

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
			.where(eqStudyGroupMemberRole(StudyGroupMemberRole.STUDY_LEADER), eqAndNotDeletedMember(memberId),
				eqAndNotDeletedStudyGroup(studyGroupId))
			.fetchFirst();

		return result != null;
	}

	public Optional<StudyGroupMember> findStudyGroupMemberById(Long studyGroupMemberId) {
		return Optional.ofNullable(
			jpaQueryFactory
				.selectFrom(studyGroupMember)
				.join(studyGroupMember.member, member)
				.join(studyGroupMember.studyGroup, studyGroup).fetchJoin()
				.where(eqStudyGroupMemberId(studyGroupMemberId), notDeletedMember(), notDeletedStudyGroup())
				.fetchFirst()
		);
	}

	@Override
	public Map<StudyGroup, List<StudyGroupQueryDto.StudyGroupMemberDto>> findStudyGroupMembers(Long studyGroupId) {
		return jpaQueryFactory
			.from(studyGroupMember)
			.join(studyGroupMember.member, member)
			.join(studyGroupMember.studyGroup, studyGroup)
			.where(notDeletedMember(), eqAndNotDeletedStudyGroup(studyGroupId))
			.orderBy(studyGroupMember.createdAt.asc())
			.transform(groupBy(studyGroup).as(
				list(Projections.constructor(
					StudyGroupQueryDto.StudyGroupMemberDto.class, studyGroupMember.id, member.profileImageUrl,
					member.nickName, member.field, member.career, member.mbti,
					studyGroupMember.studyGroupMemberRole))));
	}

	private BooleanExpression eqStudyGroupMemberId(Long studyGroupMemberId) {
		return studyGroupMemberId == null ? null : studyGroupMember.id.eq(studyGroupMemberId);
	}
}
