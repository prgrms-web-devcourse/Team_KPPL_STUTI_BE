package prgrms.project.stuti.domain.studygroup.repository.studygroup;

import static prgrms.project.stuti.domain.member.model.QMember.*;
import static prgrms.project.stuti.domain.studygroup.model.QPreferredMbti.*;
import static prgrms.project.stuti.domain.studygroup.model.QStudyGroup.*;
import static prgrms.project.stuti.domain.studygroup.model.QStudyGroupMember.*;
import static prgrms.project.stuti.domain.studygroup.repository.CommonStudyGroupBooleanExpression.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.math.NumberUtils;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import prgrms.project.stuti.domain.member.model.Mbti;
import prgrms.project.stuti.domain.studygroup.model.Region;
import prgrms.project.stuti.domain.studygroup.model.StudyGroup;
import prgrms.project.stuti.domain.studygroup.model.StudyGroupMemberRole;
import prgrms.project.stuti.domain.studygroup.model.Topic;
import prgrms.project.stuti.domain.studygroup.repository.dto.StudyGroupQueryDto;
import prgrms.project.stuti.domain.studygroup.service.StudyGroupConverter;
import prgrms.project.stuti.domain.studygroup.service.dto.StudyGroupDto;
import prgrms.project.stuti.domain.studygroup.service.response.StudyGroupsResponse;
import prgrms.project.stuti.global.page.CursorPageResponse;

@RequiredArgsConstructor
public class CustomStudyGroupRepositoryImpl implements CustomStudyGroupRepository {

	private final JPAQueryFactory jpaQueryFactory;

	@Override
	public Optional<StudyGroup> findStudyGroupById(Long studyGroupId) {
		return Optional.ofNullable(
			jpaQueryFactory
				.selectFrom(studyGroup)
				.where(equalStudyGroup(studyGroupId))
				.fetchOne());
	}

	@Override
	public List<StudyGroupQueryDto.StudyGroupDetailDto> findStudyGroupDetailById(Long studyGroupId) {
		return jpaQueryFactory
			.select(
				Projections.constructor(
					StudyGroupQueryDto.StudyGroupDetailDto.class, studyGroup.id, studyGroup.topic,
					studyGroup.title, studyGroup.imageUrl, member.id, member.profileImageUrl, member.nickName,
					member.field, member.career, member.mbti, preferredMbti.mbti, studyGroup.isOnline,
					studyGroup.region, studyGroup.studyPeriod.startDateTime, studyGroup.studyPeriod.endDateTime,
					studyGroup.numberOfMembers, studyGroup.numberOfRecruits, studyGroup.description))
			.from(studyGroup)
			.join(studyGroupMember).on(studyGroupMember.studyGroup.id.eq(studyGroup.id))
			.join(member).on(member.id.eq(studyGroupMember.member.id))
			.join(preferredMbti).on(preferredMbti.studyGroup.id.eq(studyGroup.id))
			.where(notDeletedMember(), equalStudyGroup(studyGroupId))
			.fetch();
	}

	@Override
	public CursorPageResponse<StudyGroupsResponse> dynamicFindStudyGroupsWithCursorPagination(
		StudyGroupDto.FindCondition conditionDto
	) {
		jpaQueryFactory
			.selectFrom(studyGroup)
			.join(studyGroup.preferredMBTIs, preferredMbti).fetchJoin()
			.fetch();

		List<StudyGroupQueryDto.StudyGroupsDto> contents = jpaQueryFactory
			.select(Projections.constructor(StudyGroupQueryDto.StudyGroupsDto.class, studyGroup, member.id))
			.from(studyGroup)
			.join(studyGroupMember).on(studyGroupMember.studyGroup.id.eq(studyGroup.id))
			.join(studyGroupMember.member, member)
			.where(
				lessThanLastStudyGroupId(conditionDto.lastStudyGroupId()),
				notDeletedStudyGroup(),
				equalRegion(conditionDto.region()),
				equalTopic(conditionDto.topic()),
				containsMbti(conditionDto.mbti()),
				recruitmentNotClosed(),
				hasStudyGroupMemberRole(StudyGroupMemberRole.STUDY_LEADER),
				notDeletedMember())
			.orderBy(studyGroupIdDesc())
			.limit(conditionDto.size() + NumberUtils.LONG_ONE)
			.fetch();

		boolean hasNext = contents.size() > conditionDto.size();

		if (hasNext) {
			contents.remove(contents.size() - 1);
		}

		return StudyGroupConverter.toStudyGroupsCursorPageResponse(contents, hasNext);
	}

	@Override
	public CursorPageResponse<StudyGroupsResponse> findMemberStudyGroupsWithCursorPagination(
		StudyGroupDto.FindCondition conditionDto
	) {
		jpaQueryFactory.selectFrom(studyGroup)
			.join(studyGroup.preferredMBTIs).fetchJoin()
			.fetch();

		List<StudyGroupQueryDto.StudyGroupsDto> contents = jpaQueryFactory
			.select(Projections.constructor(StudyGroupQueryDto.StudyGroupsDto.class, studyGroup, member.id))
			.from(studyGroup)
			.join(studyGroupMember).on(studyGroupMember.studyGroup.id.eq(studyGroup.id))
			.join(studyGroupMember.member, member)
			.where(
				lessThanLastStudyGroupId(conditionDto.lastStudyGroupId()),
				hasStudyGroupMemberRole(conditionDto.studyGroupMemberRole()),
				equalMemberId(conditionDto.memberId()),
				notDeletedStudyGroup())
			.orderBy(studyGroupIdDesc())
			.limit(conditionDto.size() + NumberUtils.LONG_ONE)
			.fetch();

		boolean hasNext = contents.size() > conditionDto.size();

		if (hasNext) {
			contents.remove(contents.size() - 1);
		}

		return StudyGroupConverter.toStudyGroupsCursorPageResponse(contents, hasNext);
	}

	private BooleanExpression lessThanLastStudyGroupId(Long studyGroupId) {
		return studyGroupId == null ? null : studyGroup.id.lt(studyGroupId);
	}

	private BooleanExpression equalRegion(Region region) {
		return region == null ? null : studyGroup.region.eq(region);
	}

	private BooleanExpression equalTopic(Topic topic) {
		return topic == null ? null : studyGroup.topic.eq(topic);
	}

	private BooleanExpression containsMbti(Mbti mbti) {
		return studyGroup.id
			.in(JPAExpressions
				.select(preferredMbti.studyGroup.id)
				.from(preferredMbti)
				.where(equalMbti(mbti)));
	}

	private BooleanExpression equalMbti(Mbti mbti) {
		return mbti == null ? null : preferredMbti.mbti.eq(mbti);
	}

	private BooleanExpression recruitmentNotClosed() {
		return startDateTimeHasNotPassed().and(lessThanNumberOfRecruits());
	}

	private BooleanExpression startDateTimeHasNotPassed() {
		return studyGroup.studyPeriod.startDateTime.after(LocalDateTime.now());
	}

	private BooleanExpression lessThanNumberOfRecruits() {
		return studyGroup.numberOfMembers.lt(studyGroup.numberOfRecruits);
	}

	private OrderSpecifier<Long> studyGroupIdDesc() {
		return studyGroup.id.desc();
	}
}
