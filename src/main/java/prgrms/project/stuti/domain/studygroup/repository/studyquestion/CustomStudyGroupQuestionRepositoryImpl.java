package prgrms.project.stuti.domain.studygroup.repository.studyquestion;

import static prgrms.project.stuti.domain.member.model.QMember.*;
import static prgrms.project.stuti.domain.studygroup.model.QStudyGroup.*;
import static prgrms.project.stuti.domain.studygroup.model.QStudyGroupQuestion.*;
import static prgrms.project.stuti.domain.studygroup.repository.CommonStudyGroupBooleanExpression.*;

import java.util.List;

import org.apache.commons.lang3.math.NumberUtils;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import prgrms.project.stuti.domain.studygroup.service.StudyGroupQuestionConverter;
import prgrms.project.stuti.domain.studygroup.service.dto.StudyGroupQuestionDto;
import prgrms.project.stuti.domain.studygroup.service.response.StudyGroupQuestionResponse;
import prgrms.project.stuti.domain.studygroup.service.response.StudyGroupQuestionsResponse;
import prgrms.project.stuti.global.page.PageResponse;

@RequiredArgsConstructor
public class CustomStudyGroupQuestionRepositoryImpl implements CustomStudyGroupQuestionRepository {

	private final JPAQueryFactory jpaQueryFactory;

	@Override
	public PageResponse<StudyGroupQuestionsResponse> findAllWithPagination(StudyGroupQuestionDto.PageDto pageDto) {
		List<StudyGroupQuestionResponse> parentQuestions = jpaQueryFactory
			.select(Projections.constructor(
				StudyGroupQuestionResponse.class, studyGroupQuestion.id, studyGroupQuestion.parent.id,
				member.profileImageUrl, member.id, member.nickName, studyGroupQuestion.contents,
				studyGroupQuestion.updatedAt))
			.from(studyGroupQuestion)
			.join(studyGroupQuestion.member, member)
			.join(studyGroupQuestion.studyGroup, studyGroup)
			.where(
				parentIdIsNull(true),
				equalStudyGroup(pageDto.studyGroupId()),
				lessThanLastStudyGroupQuestionId(pageDto.lastStudyGroupQuestionId()))
			.orderBy(studyGroupQuestion.id.desc())
			.limit(pageDto.size() + NumberUtils.LONG_ONE)
			.fetch();

		boolean hasNext = false;

		if (parentQuestions.size() > pageDto.size()) {
			hasNext = true;
			parentQuestions.remove(parentQuestions.size() - 1);
		}

		List<Long> parentIds = parentQuestions.stream().map(StudyGroupQuestionResponse::studyGroupQuestionId).toList();

		List<StudyGroupQuestionResponse> childrenQuestions = jpaQueryFactory
			.select(Projections.constructor(
				StudyGroupQuestionResponse.class, studyGroupQuestion.id, studyGroupQuestion.parent.id,
				member.profileImageUrl, member.id, member.nickName, studyGroupQuestion.contents,
				studyGroupQuestion.updatedAt))
			.from(studyGroupQuestion)
			.join(studyGroupQuestion.member, member)
			.join(studyGroupQuestion.studyGroup, studyGroup)
			.where(parentIdIsNull(false), studyGroupQuestion.parent.id.in(parentIds))
			.orderBy(studyGroupQuestion.id.asc())
			.fetch();

		Long totalElements = jpaQueryFactory
			.select(studyGroupQuestion.count())
			.from(studyGroupQuestion)
			.where(parentIdIsNull(true), equalStudyGroup(pageDto.studyGroupId()))
			.fetchOne();

		return StudyGroupQuestionConverter
			.toStudyGroupQuestionsPageResponse(parentQuestions, childrenQuestions, hasNext, totalElements);
	}

	private BooleanExpression parentIdIsNull(boolean flag) {
		return flag ? studyGroupQuestion.parent.id.isNull() : studyGroupQuestion.parent.id.isNotNull();
	}

	private BooleanExpression lessThanLastStudyGroupQuestionId(Long lastStudyGroupQuestionId) {
		return lastStudyGroupQuestionId == null ? null : studyGroupQuestion.id.lt(lastStudyGroupQuestionId);
	}
}
