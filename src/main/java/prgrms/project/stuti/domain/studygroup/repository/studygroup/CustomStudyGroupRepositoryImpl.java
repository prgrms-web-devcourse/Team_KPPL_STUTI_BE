package prgrms.project.stuti.domain.studygroup.repository.studygroup;

import static prgrms.project.stuti.domain.member.model.QMember.*;
import static prgrms.project.stuti.domain.studygroup.model.QStudyGroup.*;
import static prgrms.project.stuti.domain.studygroup.model.QStudyGroupMember.*;
import static prgrms.project.stuti.domain.studygroup.repository.CommonStudyGroupBooleanExpression.*;

import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.math.NumberUtils;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import prgrms.project.stuti.domain.member.model.Mbti;
import prgrms.project.stuti.domain.studygroup.model.Region;
import prgrms.project.stuti.domain.studygroup.model.StudyGroup;
import prgrms.project.stuti.domain.studygroup.model.StudyGroupMember;
import prgrms.project.stuti.domain.studygroup.model.StudyGroupMemberRole;
import prgrms.project.stuti.domain.studygroup.model.Topic;
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
	public Optional<StudyGroupMember> findStudyGroupDetailById(Long studyGroupId) {
		return Optional.ofNullable(
			jpaQueryFactory
				.selectFrom(studyGroupMember)
				.join(studyGroupMember.studyGroup, studyGroup).fetchJoin()
				.join(studyGroupMember.member, member).fetchJoin()
				.join(studyGroup.preferredMBTIs).fetchJoin()
				.where(hasStudyGroupMemberRole(StudyGroupMemberRole.STUDY_LEADER), notDeletedMember(),
					equalStudyGroup(studyGroupId))
				.fetchFirst()
		);
	}

	@Override
	public CursorPageResponse<StudyGroupsResponse> dynamicFindAllWithCursorPagination(
		StudyGroupDto.FindCondition conditionDto) {
		jpaQueryFactory.selectFrom(studyGroup).join(studyGroup.preferredMBTIs).fetchJoin().fetch();

		List<StudyGroupMember> contents = jpaQueryFactory
			.selectFrom(studyGroupMember)
			.leftJoin(studyGroupMember.member, member).fetchJoin()
			.leftJoin(studyGroupMember.studyGroup, studyGroup).fetchJoin()
			.where(
				lessThanLastStudyGroupId(conditionDto.lastStudyGroupId()),
				equalRegion(conditionDto.region()),
				equalTopic(conditionDto.topic()),
				equalMemberId(conditionDto.memberId()),
				hasStudyGroupMemberRole(conditionDto.studyGroupMemberRole()),
				notDeletedMember(),
				notDeletedStudyGroup(),
				containsMbti(conditionDto.mbti()))
			.orderBy(studyGroup.id.desc())
			.limit(conditionDto.size() + NumberUtils.LONG_ONE)
			.fetch();

		boolean hasNext = contents.size() > conditionDto.size();

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

	private BooleanExpression equalMemberId(Long memberId) {
		return memberId == null ? null : member.id.eq(memberId);
	}

	private BooleanExpression containsMbti(Mbti mbti) {
		return mbti == null ? null : studyGroup.preferredMBTIs.contains(mbti);
	}
}
