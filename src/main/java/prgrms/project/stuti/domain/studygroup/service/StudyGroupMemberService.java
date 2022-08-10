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
		validateExistingStudyGroupMember(createDto.memberId(), createDto.studyGroupId());
		StudyGroupMember studyApplicant = saveStudyApplicant(createDto.memberId(), createDto.studyGroupId());

		return StudyGroupMemberConverter.toStudyGroupMemberIdResponse(studyApplicant.getId());
	}

	@Transactional(readOnly = true)
	public StudyGroupMembersResponse getStudyGroupMembers(StudyGroupMemberDto.ReadDto readDto) {
		validateStudyLeader(readDto.memberId(), readDto.studyGroupId());
		List<StudyGroupMember> studyGroupMembers =
			studyGroupMemberRepository.findStudyGroupMembers(readDto.studyGroupId());

		return StudyGroupMemberConverter.toStudyGroupMembersResponse(studyGroupMembers);
	}

	@Transactional
	public StudyGroupMemberIdResponse acceptRequestForJoin(StudyGroupMemberDto.UpdateDto updateDto) {
		validateStudyLeader(updateDto.memberId(), updateDto.studyGroupId());
		StudyGroupMember studyGroupMember = findStudyGroupMember(updateDto.studyGroupMemberId());
		StudyGroup studyGroup = studyGroupMember.getStudyGroup();

		if (isApplicant(studyGroupMember)) {
			studyGroupMember.updateStudyGroupMemberRole(StudyGroupMemberRole.STUDY_MEMBER);
			studyGroup.decreaseNumberOfApplicants();
			studyGroup.increaseNumberOfMembers();
		}

		return StudyGroupMemberConverter.toStudyGroupMemberIdResponse(updateDto.studyGroupMemberId());
	}

	@Transactional
	public void deleteStudyGroupMember(StudyGroupMemberDto.DeleteDto deleteDto) {
		validateStudyLeader(deleteDto.memberId(), deleteDto.studyGroupId());
		StudyGroupMember studyGroupMember = findStudyGroupMember(deleteDto.studyGroupMemberId());
		StudyGroup studyGroup = studyGroupMember.getStudyGroup();

		studyGroupMemberRepository.deleteById(deleteDto.studyGroupMemberId());

		if (isApplicant(studyGroupMember)) {
			studyGroup.decreaseNumberOfApplicants();

			return;
		}

		studyGroup.decreaseNumberOfMembers();
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

	private StudyGroupMember saveStudyApplicant(Long memberId, Long studyGroupId) {
		Member member = findMember(memberId);
		StudyGroup studyGroup = findStudyGroup(studyGroupId);
		studyGroup.increaseNumberOfApplicants();

		return studyGroupMemberRepository.save(new StudyGroupMember(STUDY_APPLICANT, member, studyGroup));
	}

	private boolean isApplicant(StudyGroupMember studyGroupMember) {
		return studyGroupMember.getStudyGroupMemberRole().equals(StudyGroupMemberRole.STUDY_APPLICANT);
	}

	private Member findMember(Long memberId) {
		return memberRepository.findMemberById(memberId).orElseThrow(() -> MemberException.notFoundMember(memberId));
	}

	private StudyGroup findStudyGroup(Long studyGroupId) {
		return studyGroupRepository.findStudyGroupById(studyGroupId)
			.orElseThrow(() -> StudyGroupException.notFoundStudyGroup(studyGroupId));
	}
}
