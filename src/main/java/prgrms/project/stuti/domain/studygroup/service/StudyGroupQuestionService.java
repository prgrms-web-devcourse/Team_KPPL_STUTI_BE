package prgrms.project.stuti.domain.studygroup.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import prgrms.project.stuti.domain.member.model.Member;
import prgrms.project.stuti.domain.member.repository.MemberRepository;
import prgrms.project.stuti.domain.studygroup.model.StudyGroup;
import prgrms.project.stuti.domain.studygroup.model.StudyGroupQuestion;
import prgrms.project.stuti.domain.studygroup.repository.StudyGroupQuestionRepository;
import prgrms.project.stuti.domain.studygroup.repository.studygroup.StudyGroupRepository;
import prgrms.project.stuti.domain.studygroup.service.dto.StudyGroupQuestionDto;
import prgrms.project.stuti.domain.studygroup.service.response.StudyGroupQuestionResponse;
import prgrms.project.stuti.domain.studygroup.service.response.StudyGroupQuestionsResponse;
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
	public StudyGroupQuestionResponse createStudyGroupQuestion(StudyGroupQuestionDto.CreateDto createDto) {
		Long parentId = createDto.parentId();
		StudyGroupQuestion studyGroupQuestion = saveQuestion(createDto, parentId);

		return StudyGroupQuestionConverter.toStudyGroupQuestionResponse(studyGroupQuestion);
	}

	@Transactional(readOnly = true)
	public PageResponse<StudyGroupQuestionsResponse> getStudyGroupQuestions(StudyGroupQuestionDto.PageDto pageDto) {
		return studyGroupQuestionRepository.findAllWithPagination(pageDto);
	}

	@Transactional
	public StudyGroupQuestionResponse updateStudyGroupQuestion(StudyGroupQuestionDto.UpdateDto updateDto) {
		StudyGroupQuestion studyGroupQuestion = findStudyGroupQuestion(updateDto.studyGroupQuestionId());
		validateMember(studyGroupQuestion.getMember(), updateDto.memberId());
		validateStudyGroup(studyGroupQuestion.getStudyGroup(), updateDto.studyGroupId());
		studyGroupQuestion.updateContents(updateDto.contents());

		return StudyGroupQuestionConverter.toStudyGroupQuestionResponse(studyGroupQuestion);
	}

	@Transactional
	public StudyGroupQuestionResponse deleteStudyGroupQuestion(StudyGroupQuestionDto.DeleteDto deleteDto) {
		StudyGroupQuestion studyGroupQuestion = findStudyGroupQuestion(deleteDto.studyGroupQuestionId());
		validateMember(studyGroupQuestion.getMember(), deleteDto.memberId());
		validateStudyGroup(studyGroupQuestion.getStudyGroup(), deleteDto.studyGroupId());
		studyGroupQuestionRepository.delete(studyGroupQuestion);

		return StudyGroupQuestionConverter.toStudyGroupQuestionResponse(studyGroupQuestion);
	}

	private StudyGroupQuestion saveQuestion(StudyGroupQuestionDto.CreateDto createDto, Long parentId) {
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
