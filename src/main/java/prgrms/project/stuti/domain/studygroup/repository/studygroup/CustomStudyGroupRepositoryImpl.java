package prgrms.project.stuti.domain.studygroup.repository.studygroup;

import static prgrms.project.stuti.domain.member.model.QMember.*;
import static prgrms.project.stuti.domain.studygroup.model.QPreferredMbti.*;
import static prgrms.project.stuti.domain.studygroup.model.QStudyGroup.*;
import static prgrms.project.stuti.domain.studygroup.model.QStudyMember.*;

import java.util.List;
import java.util.Optional;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import prgrms.project.stuti.domain.studygroup.model.StudyGroup;
import prgrms.project.stuti.domain.studygroup.model.StudyMemberRole;
import prgrms.project.stuti.domain.studygroup.service.dto.StudyGroupDetailDto;

@RequiredArgsConstructor
public class CustomStudyGroupRepositoryImpl implements CustomStudyGroupRepository {

	private final JPAQueryFactory jpaQueryFactory;

	@Override
	public Optional<StudyGroup> findStudyGroupById(Long studyGroupId) {
		return Optional.ofNullable(
			jpaQueryFactory
				.selectFrom(studyGroup)
				.where(studyGroup.id.eq(studyGroupId), isNotDeleted())
				.fetchOne()
		);
	}

	@Override
	public List<StudyGroupDetailDto> findStudyGroupDetailById(Long studyGroupId) {
		return jpaQueryFactory
			.select(
				Projections.constructor(StudyGroupDetailDto.class, studyGroup.id.as("studyGroupId"), studyGroup.topic,
					studyGroup.title, studyGroup.imageUrl, studyGroup.isOnline, studyGroup.region,
					studyGroup.studyPeriod.startDateTime, studyGroup.studyPeriod.endDateTime,
					studyGroup.numberOfMembers, studyGroup.numberOfRecruits, studyGroup.description,
					member.id.as("memberId"), member.profileImageUrl, member.nickName.as("nickname"), member.field,
					member.career, member.mbti.as("mbti"), preferredMbti.mbti.as("preferredMBTI")
				))
			.from(studyMember)
			.join(studyMember.studyGroup, studyGroup)
			.join(studyMember.member, member)
			.join(preferredMbti).on(preferredMbti.studyGroup.id.eq(studyGroupId))
			.where(isLeader(), isNotDeletedMember(), isEqualIdAndNotDeletedStudyGroup(studyGroupId))
			.fetch();
	}

	private BooleanExpression isLeader() {
		return studyMember.studyMemberRole.eq(StudyMemberRole.LEADER);
	}

	private BooleanExpression isNotDeletedMember() {
		return member.isDeleted.isFalse();
	}

	public BooleanExpression isEqualIdAndNotDeletedStudyGroup(Long studyGroupId) {
		return studyGroup.id.eq(studyGroupId).and(studyGroup.isDeleted.isFalse());
	}

	private BooleanExpression isNotDeleted() {
		return studyGroup.isDeleted.isFalse();
	}
}
