package prgrms.project.stuti.domain.studygroup.repository;

import prgrms.project.stuti.domain.studygroup.service.response.StudyGroupQuestionListResponse;
import prgrms.project.stuti.global.page.PageResponse;

public interface CustomStudyGroupQuestionRepository {

	PageResponse<StudyGroupQuestionListResponse> findAllWithPagination(Long studyGroupId, Long size,
		Long lastStudyGroupQuestionId);
}
