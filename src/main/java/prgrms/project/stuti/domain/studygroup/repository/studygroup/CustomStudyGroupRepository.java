package prgrms.project.stuti.domain.studygroup.repository.studygroup;

import java.util.Optional;

import prgrms.project.stuti.domain.studygroup.model.StudyGroup;
import prgrms.project.stuti.domain.studygroup.model.StudyGroupMember;

public interface CustomStudyGroupRepository {

	Optional<StudyGroup> findStudyGroupById(Long studyGroupId);

	Optional<StudyGroupMember> findStudyGroupDetailById(Long studyGroupId);
}
