package prgrms.project.stuti.domain.studygroup.repository;

import static prgrms.project.stuti.domain.member.model.QMember.*;
import static prgrms.project.stuti.domain.studygroup.model.QStudyGroup.*;
import static prgrms.project.stuti.domain.studygroup.model.QStudyGroupQuestion.*;
import static prgrms.project.stuti.domain.studygroup.repository.CommonStudyGroupBooleanExpression.*;

import java.util.List;

import org.apache.commons.lang3.math.NumberUtils;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import prgrms.project.stuti.domain.studygroup.model.StudyGroupQuestion;
import prgrms.project.stuti.domain.studygroup.service.StudyGroupConverter;
import prgrms.project.stuti.domain.studygroup.service.response.StudyGroupQuestionListResponse;
import prgrms.project.stuti.global.page.PageResponse;

@RequiredArgsConstructor
public class CustomStudyGroupQuestionRepositoryImpl implements CustomStudyGroupQuestionRepository {

	private final JPAQueryFactory jpaQueryFactory;

	@Override
	public PageResponse<StudyGroupQuestionListResponse> findAllWithPagination(Long studyGroupId, Long size,
		Long lastStudyGroupQuestionId) {

		List<StudyGroupQuestion> studyGroupQuestions = jpaQueryFactory
			.selectFrom(studyGroupQuestion)
			.join(studyGroupQuestion.member, member).fetchJoin()
			.join(studyGroupQuestion.studyGroup, studyGroup).fetchJoin()
			.where(parentIdIsNull(), equalStudyGroup(studyGroupId),
				greaterThanLastStudyGroupQuestionId(lastStudyGroupQuestionId))
			.orderBy(studyGroupQuestion.id.asc())
			.limit(size + NumberUtils.LONG_ONE)
			.fetch();

		boolean hasNext = studyGroupQuestions.size() > size;

		if (hasNext) {
			studyGroupQuestions.remove(studyGroupQuestions.size() - 1);
		}

		Long totalElements = jpaQueryFactory
			.select(studyGroupQuestion.count())
			.from(studyGroupQuestion)
			.where(parentIdIsNull(), equalStudyGroup(studyGroupId))
			.fetchOne();

		return StudyGroupConverter.toStudyGroupQuestionPageResponse(studyGroupQuestions, hasNext, totalElements);
	}

	private BooleanExpression parentIdIsNull() {
		return studyGroupQuestion.parent.id.isNull();
	}

	private BooleanExpression greaterThanLastStudyGroupQuestionId(Long lastStudyGroupQuestionId) {
		return lastStudyGroupQuestionId == null ? null : studyGroupQuestion.id.gt(lastStudyGroupQuestionId);
	}
}
