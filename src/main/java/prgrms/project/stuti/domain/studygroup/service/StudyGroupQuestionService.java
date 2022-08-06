package prgrms.project.stuti.domain.studygroup.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import prgrms.project.stuti.domain.member.model.Member;
import prgrms.project.stuti.domain.member.repository.MemberRepository;
import prgrms.project.stuti.domain.studygroup.service.response.StudyGroupQuestionListResponse;
import prgrms.project.stuti.domain.studygroup.model.StudyGroup;
import prgrms.project.stuti.domain.studygroup.model.StudyGroupQuestion;
import prgrms.project.stuti.domain.studygroup.repository.StudyGroupQuestionRepository;
import prgrms.project.stuti.domain.studygroup.repository.studygroup.StudyGroupRepository;
import prgrms.project.stuti.domain.studygroup.service.dto.StudyGroupQuestionCreateDto;
import prgrms.project.stuti.domain.studygroup.service.dto.StudyGroupQuestionUpdateDto;
import prgrms.project.stuti.domain.studygroup.service.response.StudyGroupQuestionResponse;
import prgrms.project.stuti.global.error.exception.MemberException;
import prgrms.project.stuti.global.error.exception.StudyGroupException;
import prgrms.project.stuti.global.page.PageResponse;

@Service
@RequiredArgsConstructor
public class StudyGroupQuestionService {

	private final MemberRepository memberRepository;
	private final StudyGroupRepository studyGroupRepository;
	private final StudyGroupQuestionRepository studyGroupQuestionRepository;

	@Transactional
	public StudyGroupQuestionResponse createStudyGroupQuestion(StudyGroupQuestionCreateDto createDto) {
		Long parentId = createDto.parentId();
		StudyGroupQuestion studyGroupQuestion = saveQuestion(createDto, parentId);

		return StudyGroupConverter.toStudyGroupQuestionResponse(studyGroupQuestion);
	}

	@Transactional(readOnly = true)
	public PageResponse<StudyGroupQuestionListResponse> getStudyGroupQuestions(Long studyGroupId, Long size,
		Long lastStudyGroupQuestionId) {
		return studyGroupQuestionRepository.findAllWithPagination(studyGroupId, size, lastStudyGroupQuestionId);
	}

	@Transactional
	public StudyGroupQuestionResponse updateStudyGroupQuestion(StudyGroupQuestionUpdateDto updateDto) {
		StudyGroupQuestion studyGroupQuestion = findStudyGroupQuestion(updateDto.studyGroupQuestionId());
		updateQuestion(updateDto, studyGroupQuestion);

		return StudyGroupConverter.toStudyGroupQuestionResponse(studyGroupQuestion);
	}

	@Transactional
	public StudyGroupQuestionResponse deleteStudyGroupQuestion(Long memberId, Long studyGroupId,
		Long studyGroupQuestionId) {
		StudyGroupQuestion studyGroupQuestion = findStudyGroupQuestion(studyGroupQuestionId);
		deleteQuestion(memberId, studyGroupId, studyGroupQuestion);

		return StudyGroupConverter.toStudyGroupQuestionResponse(studyGroupQuestion);
	}

	private StudyGroupQuestion saveQuestion(StudyGroupQuestionCreateDto createDto, Long parentId) {
		StudyGroupQuestion parent = parentId == null
			? null
			: findStudyGroupQuestion(parentId);
		Member member = findMember(createDto.memberId());
		StudyGroup studyGroup = findStudyGroup(createDto.studyGroupId());

		return studyGroupQuestionRepository.save(
			new StudyGroupQuestion(createDto.contents(), parent, member, studyGroup));
	}

	private StudyGroupQuestion findStudyGroupQuestion(Long studyGroupQuestionId) {
		return studyGroupQuestionRepository.findStudyGroupQuestionById(studyGroupQuestionId)
			.orElseThrow(() -> StudyGroupException.notFoundStudyGroupQuestion(studyGroupQuestionId));
	}

	private void deleteQuestion(Long memberId, Long studyGroupId, StudyGroupQuestion studyGroupQuestion) {
		Member member = studyGroupQuestion.getMember();
		StudyGroup studyGroup = studyGroupQuestion.getStudyGroup();

		validateMember(member, memberId);
		validateStudyGroup(studyGroup, studyGroupId);

		studyGroupQuestionRepository.delete(studyGroupQuestion);
	}

	private void updateQuestion(StudyGroupQuestionUpdateDto updateDto, StudyGroupQuestion studyGroupQuestion) {
		validateMember(studyGroupQuestion.getMember(), updateDto.memberId());
		validateStudyGroup(studyGroupQuestion.getStudyGroup(), updateDto.studyGroupId());

		studyGroupQuestion.updateContents(updateDto.contents());
	}

	private Member findMember(Long memberId) {
		return memberRepository.findMemberById(memberId).orElseThrow(() -> MemberException.notFoundMember(memberId));
	}

	private StudyGroup findStudyGroup(Long studyGroupId) {
		return studyGroupRepository.findStudyGroupById(studyGroupId)
			.orElseThrow(() -> StudyGroupException.notFoundStudyGroup(studyGroupId));
	}

	private void validateMember(Member member, Long memberId) {
		if (!member.getId().equals(memberId)) {
			throw StudyGroupException.notMatchWriter(memberId);
		}
	}

	private void validateStudyGroup(StudyGroup studyGroup, Long studyGroupId) {
		if (!studyGroup.getId().equals(studyGroupId)) {
			throw StudyGroupException.notMatchStudyGroup(studyGroupId);
		}
	}
}
