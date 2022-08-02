package prgrms.project.stuti.domain.studygroup.service.studymember;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import prgrms.project.stuti.domain.studygroup.model.StudyMember;
import prgrms.project.stuti.domain.studygroup.model.StudyMemberRole;
import prgrms.project.stuti.domain.studygroup.repository.studymember.StudyMemberRepository;
import prgrms.project.stuti.domain.studygroup.service.response.StudyMemberIdResponse;
import prgrms.project.stuti.global.error.exception.StudyGroupException;

@Service
@RequiredArgsConstructor
public class StudyMemberService {

	private final StudyMemberRepository studyMemberRepository;

	@Transactional
	public StudyMemberIdResponse acceptRequestForJoin(Long memberId, Long studyGroupId, Long studyMemberId) {
		validateLeader(memberId, studyGroupId);
		StudyMember studyMember = findStudyMember(studyMemberId);
		studyMember.updateStudyMemberRole(StudyMemberRole.STUDY_MEMBER);

		return StudyMemberConverter.toStudyMemberIdResponse(studyMemberId);
	}

	private StudyMember findStudyMember(Long studyMemberId) {
		return studyMemberRepository.findStudyMemberById(studyMemberId)
			.orElseThrow(() -> StudyGroupException.notFoundStudyMember(studyMemberId));
	}

	private void validateLeader(Long memberId, Long studyGroupId) {
		boolean isLeader = studyMemberRepository.isLeader(memberId, studyGroupId);

		if (!isLeader) {
			throw StudyGroupException.notLeader(memberId, studyGroupId);
		}
	}
}
