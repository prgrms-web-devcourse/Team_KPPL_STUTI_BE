package prgrms.project.stuti.domain.studygroup.repository.studymember;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import prgrms.project.stuti.domain.studygroup.model.StudyGroup;
import prgrms.project.stuti.domain.studygroup.model.StudyGroupMember;
import prgrms.project.stuti.domain.studygroup.repository.dto.StudyGroupQueryDto;

public interface CustomStudyGroupMemberRepository {

	boolean isStudyLeader(Long memberId, Long studyGroupId);

	Optional<StudyGroupMember> findStudyGroupMemberById(Long studyGroupMemberId);

	Map<StudyGroup, List<StudyGroupQueryDto.StudyGroupMemberDto>> findStudyGroupMembers(Long studyGroupId);
}
