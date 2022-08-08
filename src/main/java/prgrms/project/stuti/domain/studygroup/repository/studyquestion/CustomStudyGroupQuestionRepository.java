package prgrms.project.stuti.domain.studygroup.repository.studyquestion;

import prgrms.project.stuti.domain.studygroup.service.dto.StudyGroupQuestionDto;
import prgrms.project.stuti.domain.studygroup.service.response.StudyGroupQuestionsResponse;
import prgrms.project.stuti.global.page.PageResponse;

public interface CustomStudyGroupQuestionRepository {

	PageResponse<StudyGroupQuestionsResponse> findAllWithPagination(StudyGroupQuestionDto.PageDto pageDto);
}
