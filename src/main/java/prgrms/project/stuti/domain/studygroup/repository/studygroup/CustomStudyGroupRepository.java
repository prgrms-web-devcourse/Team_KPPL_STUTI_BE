package prgrms.project.stuti.domain.studygroup.repository.studygroup;

import java.util.List;
import java.util.Optional;

import prgrms.project.stuti.domain.studygroup.model.StudyGroup;
import prgrms.project.stuti.domain.studygroup.service.dto.StudyGroupDetailDto;

public interface CustomStudyGroupRepository {

	Optional<StudyGroup> findStudyGroupById(Long studyGroupId);

	List<StudyGroupDetailDto> findStudyGroupDetailById(Long studyGroupId);
}
