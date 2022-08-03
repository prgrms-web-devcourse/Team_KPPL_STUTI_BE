package prgrms.project.stuti.domain.studygroup.repository.studymember;

import java.util.Optional;

import prgrms.project.stuti.domain.studygroup.model.StudyMember;

public interface CustomStudyMemberRepository {

	boolean isLeader(Long memberId, Long studyGroupId);

	Optional<StudyMember> findStudyMemberById(Long studyMemberId);
}
