package prgrms.project.stuti.domain.studygroup.service;

import static prgrms.project.stuti.domain.studygroup.model.StudyGroupMemberRole.*;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import prgrms.project.stuti.domain.member.model.Member;
import prgrms.project.stuti.domain.member.repository.MemberRepository;
import prgrms.project.stuti.domain.studygroup.model.StudyGroup;
import prgrms.project.stuti.domain.studygroup.model.StudyGroupMember;
import prgrms.project.stuti.domain.studygroup.model.StudyGroupMemberRole;
import prgrms.project.stuti.domain.studygroup.repository.studygroup.StudyGroupRepository;
import prgrms.project.stuti.domain.studygroup.repository.studymember.StudyGroupMemberRepository;
import prgrms.project.stuti.domain.studygroup.service.response.StudyGroupMemberIdResponse;
import prgrms.project.stuti.domain.studygroup.service.response.StudyGroupMemberManagementResponse;
import prgrms.project.stuti.global.error.exception.MemberException;
import prgrms.project.stuti.global.error.exception.StudyGroupException;

@Service
@RequiredArgsConstructor
public class StudyGroupMemberService {

	private final MemberRepository memberRepository;
	private final StudyGroupRepository studyGroupRepository;
	private final StudyGroupMemberRepository studyGroupMemberRepository;

	@Transactional
	public StudyGroupMemberIdResponse applyForJoinStudyGroup(Long memberId, Long studyGroupId) {
		validateExistingStudyGroupMember(memberId, studyGroupId);
		Long studyGroupMemberId = saveStudyApplicant(memberId, studyGroupId);

		return StudyGroupConverter.toStudyGroupMemberIdResponse(studyGroupMemberId);
	}

	@Transactional(readOnly = true)
	public StudyGroupMemberManagementResponse getStudyGroupMembers(Long memberId, Long studyGroupId) {
		List<StudyGroupMember> studyGroupMembers = studyGroupMemberRepository.findStudyGroupMembers(memberId,
			studyGroupId);

		return StudyGroupConverter.toStudyGroupMemberManagementResponse(studyGroupMembers);
	}

	@Transactional
	public StudyGroupMemberIdResponse acceptRequestForJoin(Long memberId, Long studyGroupId, Long studyGroupMemberId) {
		validateStudyLeader(memberId, studyGroupId);
		StudyGroupMember studyGroupMember = findStudyGroupMember(studyGroupMemberId);
		StudyGroup studyGroup = studyGroupMember.getStudyGroup();

		if (!studyGroupMember.getStudyGroupMemberRole().equals(StudyGroupMemberRole.STUDY_APPLICANT)) {
			studyGroupMember.updateStudyGroupMemberRole(StudyGroupMemberRole.STUDY_MEMBER);
			studyGroup.increaseNumberOfMembers();
		}

		return StudyGroupConverter.toStudyGroupMemberIdResponse(studyGroupMemberId);
	}

	@Transactional
	public void deleteStudyGroupMember(Long memberId, Long studyGroupId, Long studyGroupMemberId) {
		validateStudyLeader(memberId, studyGroupId);
		studyGroupMemberRepository.deleteById(studyGroupMemberId);
	}

	private StudyGroupMember findStudyGroupMember(Long studyGroupMemberId) {
		return studyGroupMemberRepository.findStudyGroupMemberById(studyGroupMemberId)
			.orElseThrow(() -> StudyGroupException.notFoundStudyGroupMember(studyGroupMemberId));
	}

	private void validateStudyLeader(Long memberId, Long studyGroupId) {
		boolean isStudyLeader = studyGroupMemberRepository.isStudyLeader(memberId, studyGroupId);

		if (!isStudyLeader) {
			throw StudyGroupException.notStudyLeader(memberId, studyGroupId);
		}
	}

	private void validateExistingStudyGroupMember(Long memberId, Long studyGroupId) {
		boolean isExists = studyGroupMemberRepository.existsByMemberIdAndStudyGroupId(memberId, studyGroupId);

		if (isExists) {
			throw StudyGroupException.existingStudyMember(memberId, studyGroupId);
		}
	}

	private Long saveStudyApplicant(Long memberId, Long studyGroupId) {
		Member member = findMember(memberId);
		StudyGroup studyGroup = findStudyGroup(studyGroupId);

		return studyGroupMemberRepository.save(new StudyGroupMember(
			STUDY_APPLICANT, member, studyGroup))
			.getId();
	}

	private Member findMember(Long memberId) {
		return memberRepository.findMemberById(memberId).orElseThrow(() -> MemberException.notFoundMember(memberId));
	}

	private StudyGroup findStudyGroup(Long studyGroupId) {
		return studyGroupRepository.findStudyGroupById(studyGroupId)
			.orElseThrow(() -> StudyGroupException.notFoundStudyGroup(studyGroupId));
	}
}
