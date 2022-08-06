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
import prgrms.project.stuti.domain.studygroup.service.dto.StudyGroupMemberDto;
import prgrms.project.stuti.domain.studygroup.service.response.StudyGroupMemberIdResponse;
import prgrms.project.stuti.domain.studygroup.service.response.StudyGroupMembersResponse;
import prgrms.project.stuti.global.error.exception.MemberException;
import prgrms.project.stuti.global.error.exception.StudyGroupException;

@Service
@RequiredArgsConstructor
public class StudyGroupMemberService {

	private final MemberRepository memberRepository;
	private final StudyGroupRepository studyGroupRepository;
	private final StudyGroupMemberRepository studyGroupMemberRepository;

	@Transactional
	public StudyGroupMemberIdResponse applyForJoinStudyGroup(StudyGroupMemberDto.CreateDto createDto) {
		Long memberId = createDto.memberId();
		Long studyGroupId = createDto.studyGroupId();
		validateExistingStudyGroupMember(memberId, studyGroupId);
		Long studyGroupMemberId = saveStudyApplicant(memberId, studyGroupId);

		return StudyGroupMemberConverter.toStudyGroupMemberIdResponse(studyGroupMemberId);
	}

	@Transactional(readOnly = true)
	public StudyGroupMembersResponse getStudyGroupMembers(StudyGroupMemberDto.ReadDto readDto) {
		Long memberId = readDto.memberId();
		Long studyGroupId = readDto.studyGroupId();
		validateStudyLeader(memberId, studyGroupId);
		List<StudyGroupMember> studyGroupMembers = studyGroupMemberRepository.findStudyGroupMembers(studyGroupId);

		return StudyGroupMemberConverter.toStudyGroupMembersResponse(studyGroupMembers);
	}

	@Transactional
	public StudyGroupMemberIdResponse acceptRequestForJoin(StudyGroupMemberDto.UpdateDto updateDto) {
		Long memberId = updateDto.memberId();
		Long studyGroupId = updateDto.studyGroupId();
		Long studyGroupMemberId = updateDto.studyGroupMemberId();

		validateStudyLeader(memberId, studyGroupId);
		StudyGroupMember studyGroupMember = findStudyGroupMember(studyGroupMemberId);
		StudyGroup studyGroup = studyGroupMember.getStudyGroup();

		if (studyGroupMember.getStudyGroupMemberRole().equals(StudyGroupMemberRole.STUDY_APPLICANT)) {
			studyGroupMember.updateStudyGroupMemberRole(StudyGroupMemberRole.STUDY_MEMBER);
			studyGroup.increaseNumberOfMembers();
		}

		return StudyGroupMemberConverter.toStudyGroupMemberIdResponse(studyGroupMemberId);
	}

	@Transactional
	public void deleteStudyGroupMember(StudyGroupMemberDto.DeleteDto deleteDto) {
		validateStudyLeader(deleteDto.memberId(), deleteDto.studyGroupId());
		studyGroupMemberRepository.deleteById(deleteDto.studyGroupMemberId());
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

		return studyGroupMemberRepository.save(new StudyGroupMember(STUDY_APPLICANT, member, studyGroup)).getId();
	}

	private Member findMember(Long memberId) {
		return memberRepository.findMemberById(memberId).orElseThrow(() -> MemberException.notFoundMember(memberId));
	}

	private StudyGroup findStudyGroup(Long studyGroupId) {
		return studyGroupRepository.findStudyGroupById(studyGroupId)
			.orElseThrow(() -> StudyGroupException.notFoundStudyGroup(studyGroupId));
	}
}
