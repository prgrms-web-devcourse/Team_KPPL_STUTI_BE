package prgrms.project.stuti.domain.studygroup.repository.studygroup;

import static prgrms.project.stuti.domain.member.model.QMember.*;
import static prgrms.project.stuti.domain.studygroup.model.QStudyGroup.*;
import static prgrms.project.stuti.domain.studygroup.model.QStudyMember.*;
import static prgrms.project.stuti.domain.studygroup.repository.CommonStudyGroupBooleanExpression.*;

import java.util.Optional;

import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import prgrms.project.stuti.domain.studygroup.model.StudyGroup;
import prgrms.project.stuti.domain.studygroup.model.StudyMember;
import prgrms.project.stuti.domain.studygroup.model.StudyMemberRole;

@RequiredArgsConstructor
public class CustomStudyGroupRepositoryImpl implements CustomStudyGroupRepository {

	private final JPAQueryFactory jpaQueryFactory;

	@Override
	public Optional<StudyGroup> findStudyGroupById(Long studyGroupId) {
		return Optional.ofNullable(
			jpaQueryFactory
				.selectFrom(studyGroup)
				.where(isEqualIdAndNotDeletedStudyGroup(studyGroupId))
				.fetchOne());
	}

	@Override
	public Optional<StudyMember> findStudyGroupDetailById(Long studyGroupId) {
		return Optional.ofNullable(
			jpaQueryFactory
				.selectFrom(studyMember)
				.join(studyMember.studyGroup, studyGroup).fetchJoin()
				.join(studyMember.member, member).fetchJoin()
				.join(studyGroup.preferredMBTIs).fetchJoin()
				.where(hasStudyMemberRole(StudyMemberRole.LEADER), isNotDeletedMember(),
					isEqualIdAndNotDeletedStudyGroup(studyGroupId))
				.fetchFirst()
		);
	}
}
