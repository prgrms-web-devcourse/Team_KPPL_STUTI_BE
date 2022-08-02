package prgrms.project.stuti.domain.studygroup.repository.studymember;

import org.springframework.data.jpa.repository.JpaRepository;

import prgrms.project.stuti.domain.studygroup.model.StudyMember;

public interface StudyMemberRepository extends JpaRepository<StudyMember, Long>, CustomStudyMemberRepository {

	boolean existsByMemberIdAndStudyGroupId(Long memberId, Long studyGroupId);
}
