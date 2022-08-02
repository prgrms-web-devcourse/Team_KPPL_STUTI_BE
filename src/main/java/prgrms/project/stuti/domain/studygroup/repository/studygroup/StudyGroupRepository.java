package prgrms.project.stuti.domain.studygroup.repository.studygroup;

import org.springframework.data.jpa.repository.JpaRepository;

import prgrms.project.stuti.domain.studygroup.model.StudyGroup;

public interface StudyGroupRepository extends JpaRepository<StudyGroup, Long>, CustomStudyGroupRepository {
}
