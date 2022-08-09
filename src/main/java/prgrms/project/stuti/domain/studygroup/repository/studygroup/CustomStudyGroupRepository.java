package prgrms.project.stuti.domain.studygroup.repository.studygroup;

import java.util.Optional;

import prgrms.project.stuti.domain.studygroup.model.StudyGroup;
import prgrms.project.stuti.domain.studygroup.model.StudyGroupMember;
import prgrms.project.stuti.domain.studygroup.service.dto.StudyGroupDto;
import prgrms.project.stuti.domain.studygroup.service.response.StudyGroupsResponse;
import prgrms.project.stuti.global.page.CursorPageResponse;

public interface CustomStudyGroupRepository {

	Optional<StudyGroup> findStudyGroupById(Long studyGroupId);

	Optional<StudyGroupMember> findStudyGroupDetailById(Long studyGroupId);

	CursorPageResponse<StudyGroupsResponse> dynamicFindStudyGroupsWithCursorPagination(
		StudyGroupDto.FindCondition conditionDto);

	CursorPageResponse<StudyGroupsResponse> findMyStudyGroupsWithCursorPagination(
		StudyGroupDto.FindCondition conditionDto);
}
