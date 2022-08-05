package prgrms.project.stuti.domain.studygroup.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import prgrms.project.stuti.domain.studygroup.model.StudyGroupQuestion;

public interface StudyGroupQuestionRepository extends JpaRepository<StudyGroupQuestion, Long> {

	@EntityGraph(attributePaths = {"member", "studyGroup"})
	StudyGroupQuestion findStudyGroupQuestionById(Long studyGroupQuestionId);
}
