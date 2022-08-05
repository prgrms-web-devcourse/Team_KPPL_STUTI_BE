package prgrms.project.stuti.domain.studygroup.repository.studymember;

import org.springframework.data.jpa.repository.JpaRepository;

import prgrms.project.stuti.domain.studygroup.model.StudyGroupMember;

public interface StudyGroupMemberRepository
	extends JpaRepository<StudyGroupMember, Long>, CustomStudyGroupMemberRepository {

	boolean existsByMemberIdAndStudyGroupId(Long memberId, Long studyGroupId);
}
