package prgrms.project.stuti.domain.studygroup.repository.studygroup;

import java.util.Optional;

import prgrms.project.stuti.domain.studygroup.model.StudyGroup;

public interface CustomStudyGroupRepository {

	Optional<StudyGroup> findStudyGroupById(Long studyGroupId);
}
