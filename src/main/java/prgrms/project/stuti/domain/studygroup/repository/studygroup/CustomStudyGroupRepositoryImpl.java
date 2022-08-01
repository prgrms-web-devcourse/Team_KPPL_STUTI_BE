package prgrms.project.stuti.domain.studygroup.repository.studygroup;

import static prgrms.project.stuti.domain.studygroup.model.QStudyGroup.*;

import java.util.Optional;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import prgrms.project.stuti.domain.studygroup.model.StudyGroup;

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

	private BooleanExpression isNotDeleted() {
		return studyGroup.isDeleted.isFalse();
	}
}
