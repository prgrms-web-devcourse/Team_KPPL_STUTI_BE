package prgrms.project.stuti.domain.studygroup.service;

import static prgrms.project.stuti.domain.studygroup.model.StudyGroupMemberRole.*;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import prgrms.project.stuti.domain.member.model.Member;
import prgrms.project.stuti.domain.member.repository.MemberRepository;
import prgrms.project.stuti.domain.studygroup.model.StudyGroup;
import prgrms.project.stuti.domain.studygroup.model.StudyGroupMember;
import prgrms.project.stuti.domain.studygroup.model.StudyGroupMemberRole;
import prgrms.project.stuti.domain.studygroup.repository.dto.StudyGroupQueryDto;
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
		Map<StudyGroup, List<StudyGroupQueryDto.StudyGroupMemberDto>> studyGroupMemberDtoMap =
			studyGroupMemberRepository.findStudyGroupMembers(readDto.studyGroupId());
		StudyGroup studyGroup = studyGroupMemberDtoMap
			.keySet()
			.stream()
			.findFirst()
			.orElseThrow(() ->  StudyGroupException.notFoundStudyGroup(readDto.studyGroupId()));

		validateStudyLeader(readDto.memberId(), readDto.studyGroupId());

		return StudyGroupMemberConverter.toStudyGroupMembersResponse(studyGroup, studyGroupMemberDtoMap.get(studyGroup));
	}

	@Transactional
	public StudyGroupMemberIdResponse acceptRequestForJoin(StudyGroupMemberDto.UpdateDto updateDto) {
		validateStudyLeader(updateDto.memberId(), updateDto.studyGroupId());
		StudyGroupMember studyGroupMember = findStudyGroupMember(updateDto.studyGroupMemberId());
		StudyGroup studyGroup = studyGroupMember.getStudyGroup();

		if (isApplicant(studyGroupMember)) {
			updateRoleToStudyMember(studyGroupMember, studyGroup);
		}

		return StudyGroupMemberConverter.toStudyGroupMemberIdResponse(updateDto.studyGroupMemberId());
	}

	@Transactional
	public void deleteStudyGroupMember(StudyGroupMemberDto.DeleteDto deleteDto) {
		validateStudyLeader(deleteDto.memberId(), deleteDto.studyGroupId());
		StudyGroupMember studyGroupMember = findStudyGroupMember(deleteDto.studyGroupMemberId());
		StudyGroup studyGroup = studyGroupMember.getStudyGroup();

		deleteById(deleteDto, studyGroupMember, studyGroup);
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
		Member member = findMemberById(memberId);
		StudyGroup studyGroup = findStudyGroupById(studyGroupId);
		studyGroup.increaseNumberOfApplicants();

		return studyGroupMemberRepository.save(new StudyGroupMember(STUDY_APPLICANT, member, studyGroup));
	}

	private boolean isApplicant(StudyGroupMember studyGroupMember) {
		return studyGroupMember.getStudyGroupMemberRole().equals(StudyGroupMemberRole.STUDY_APPLICANT);
	}

	private void updateRoleToStudyMember(StudyGroupMember studyGroupMember, StudyGroup studyGroup) {
		studyGroupMember.updateStudyGroupMemberRole(StudyGroupMemberRole.STUDY_MEMBER);
		studyGroup.decreaseNumberOfApplicants();
		studyGroup.increaseNumberOfMembers();
	}

	private void deleteById(
		StudyGroupMemberDto.DeleteDto deleteDto, StudyGroupMember studyGroupMember, StudyGroup studyGroup
	) {
		studyGroupMemberRepository.deleteById(deleteDto.studyGroupMemberId());

		if (isApplicant(studyGroupMember)) {
			studyGroup.decreaseNumberOfApplicants();

			return;
		}

		studyGroup.decreaseNumberOfMembers();
	}

	private Member findMemberById(Long memberId) {
		return memberRepository.findMemberById(memberId).orElseThrow(() -> MemberException.notFoundMember(memberId));
	}

	private StudyGroup findStudyGroupById(Long studyGroupId) {
		return studyGroupRepository.findStudyGroupById(studyGroupId)
			.orElseThrow(() -> StudyGroupException.notFoundStudyGroup(studyGroupId));
	}
}
