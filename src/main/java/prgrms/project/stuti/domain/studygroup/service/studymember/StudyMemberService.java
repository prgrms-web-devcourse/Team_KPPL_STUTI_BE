package prgrms.project.stuti.domain.studygroup.service.studymember;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import prgrms.project.stuti.domain.member.model.Member;
import prgrms.project.stuti.domain.member.repository.MemberRepository;
import prgrms.project.stuti.domain.studygroup.model.StudyGroup;
import prgrms.project.stuti.domain.studygroup.model.StudyMember;
import prgrms.project.stuti.domain.studygroup.model.StudyMemberRole;
import prgrms.project.stuti.domain.studygroup.repository.studygroup.StudyGroupRepository;
import prgrms.project.stuti.domain.studygroup.repository.studymember.StudyMemberRepository;
import prgrms.project.stuti.domain.studygroup.service.response.StudyMemberIdResponse;
import prgrms.project.stuti.global.error.exception.MemberException;
import prgrms.project.stuti.global.error.exception.StudyGroupException;

@Service
@RequiredArgsConstructor
public class StudyMemberService {

	private final MemberRepository memberRepository;
	private final StudyGroupRepository studyGroupRepository;
	private final StudyMemberRepository studyMemberRepository;

	@Transactional
	public StudyMemberIdResponse applyForJoinStudyGroup(Long memberId, Long studyGroupId) {
		validateExistingStudyMember(memberId, studyGroupId);
		Long studyMemberId = saveStudyGroupApplicant(memberId, studyGroupId);

		return StudyMemberConverter.toStudyMemberIdResponse(studyMemberId);
	}

	@Transactional
	public StudyMemberIdResponse acceptRequestForJoin(Long memberId, Long studyGroupId, Long studyMemberId) {
		validateLeader(memberId, studyGroupId);
		StudyMember studyMember = findStudyMember(studyMemberId);
		studyMember.updateStudyMemberRole(StudyMemberRole.STUDY_MEMBER);

		return StudyMemberConverter.toStudyMemberIdResponse(studyMemberId);
	}

	@Transactional
	public void deleteStudyMember(Long memberId, Long studyGroupId, Long studyMemberId) {
		validateLeader(memberId, studyGroupId);
		studyMemberRepository.deleteById(studyMemberId);
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

	private void validateExistingStudyMember(Long memberId, Long studyGroupId) {
		boolean isExists = studyMemberRepository.existsByMemberIdAndStudyGroupId(memberId, studyGroupId);

		if (isExists) {
			throw StudyGroupException.existingStudyMember(memberId, studyGroupId);
		}
	}

	private Long saveStudyGroupApplicant(Long memberId, Long studyGroupId) {
		Member member = findMember(memberId);
		StudyGroup studyGroup = findStudyGroup(studyGroupId);

		return studyMemberRepository.save(new StudyMember(StudyMemberRole.APPLICANT, member, studyGroup)).getId();
	}

	private Member findMember(Long memberId) {
		return memberRepository.findMemberById(memberId).orElseThrow(() -> MemberException.notFoundMember(memberId));
	}

	private StudyGroup findStudyGroup(Long studyGroupId) {
		return studyGroupRepository.findStudyGroupById(studyGroupId)
			.orElseThrow(() -> StudyGroupException.notFoundStudyGroup(studyGroupId));
	}
}
