package prgrms.project.stuti.domain.studygroup.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import prgrms.project.stuti.domain.studygroup.model.Question;

public interface QuestionRepository extends JpaRepository<Question, Long> {
}
