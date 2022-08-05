package prgrms.project.stuti.domain.studygroup.repository.studygroup;

import static prgrms.project.stuti.domain.member.model.QMember.*;
import static prgrms.project.stuti.domain.studygroup.model.QStudyGroup.*;
import static prgrms.project.stuti.domain.studygroup.model.QStudyGroupMember.*;
import static prgrms.project.stuti.domain.studygroup.repository.CommonStudyGroupBooleanExpression.*;

import java.util.Optional;

import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import prgrms.project.stuti.domain.studygroup.model.StudyGroup;
import prgrms.project.stuti.domain.studygroup.model.StudyGroupMember;
import prgrms.project.stuti.domain.studygroup.model.StudyGroupMemberRole;

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
	public Optional<StudyGroupMember> findStudyGroupDetailById(Long studyGroupId) {
		return Optional.ofNullable(
			jpaQueryFactory
				.selectFrom(studyGroupMember)
				.join(studyGroupMember.studyGroup, studyGroup).fetchJoin()
				.join(studyGroupMember.member, member).fetchJoin()
				.join(studyGroup.preferredMBTIs).fetchJoin()
				.where(hasStudyGroupMemberRole(StudyGroupMemberRole.STUDY_LEADER), isNotDeletedMember(),
					isEqualIdAndNotDeletedStudyGroup(studyGroupId))
				.fetchFirst()
		);
	}
}
