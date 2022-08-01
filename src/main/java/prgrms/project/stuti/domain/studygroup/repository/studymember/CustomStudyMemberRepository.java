package prgrms.project.stuti.domain.studygroup.repository.studymember;

public interface CustomStudyMemberRepository {

	boolean isLeader(Long memberId, Long studyGroupId);
}
