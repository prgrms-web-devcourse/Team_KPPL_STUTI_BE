package prgrms.project.stuti.domain.studygroup.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import prgrms.project.stuti.domain.studygroup.model.StudyMember;

public interface StudyMemberRepository extends JpaRepository<StudyMember, Long> {
}