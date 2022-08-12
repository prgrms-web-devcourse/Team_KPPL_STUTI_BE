package prgrms.project.stuti.domain.studygroup.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import prgrms.project.stuti.domain.member.model.Member;
import prgrms.project.stuti.domain.member.repository.MemberRepository;
import prgrms.project.stuti.domain.studygroup.model.StudyGroup;
import prgrms.project.stuti.domain.studygroup.model.StudyGroupQuestion;
import prgrms.project.stuti.domain.studygroup.repository.studygroup.StudyGroupRepository;
import prgrms.project.stuti.domain.studygroup.repository.studyquestion.StudyGroupQuestionRepository;
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
		StudyGroupQuestion studyGroupQuestion = saveStudyGroupQuestion(createDto);

		return StudyGroupQuestionConverter.toStudyGroupQuestionResponse(studyGroupQuestion);
	}

	@Transactional(readOnly = true)
	public PageResponse<StudyGroupQuestionsResponse> getStudyGroupQuestions(StudyGroupQuestionDto.PageDto pageDto) {
		return studyGroupQuestionRepository.findAllWithPagination(pageDto);
	}

	@Transactional
	public StudyGroupQuestionResponse updateStudyGroupQuestion(StudyGroupQuestionDto.UpdateDto updateDto) {
		StudyGroupQuestion studyGroupQuestion = findStudyGroupQuestionById(updateDto.studyGroupQuestionId());
		validateWriter(studyGroupQuestion.getMember(), updateDto.memberId());
		validateStudyGroup(studyGroupQuestion.getStudyGroup(), updateDto.studyGroupId());
		studyGroupQuestion.updateContents(updateDto.contents());

		return StudyGroupQuestionConverter.toStudyGroupQuestionResponse(studyGroupQuestion);
	}

	@Transactional
	public StudyGroupQuestionResponse deleteStudyGroupQuestion(StudyGroupQuestionDto.DeleteDto deleteDto) {
		StudyGroupQuestion studyGroupQuestion = findStudyGroupQuestionById(deleteDto.studyGroupQuestionId());
		validateWriter(studyGroupQuestion.getMember(), deleteDto.memberId());
		validateStudyGroup(studyGroupQuestion.getStudyGroup(), deleteDto.studyGroupId());
		studyGroupQuestionRepository.delete(studyGroupQuestion);

		return StudyGroupQuestionConverter.toStudyGroupQuestionResponse(studyGroupQuestion);
	}

	private StudyGroupQuestion saveStudyGroupQuestion(StudyGroupQuestionDto.CreateDto createDto) {
		StudyGroupQuestion parent = createDto.parentId() == null
			? null
			: findStudyGroupQuestionById(createDto.parentId());
		Member member = findMemberById(createDto.memberId());
		StudyGroup studyGroup = findStudyGroupById(createDto.studyGroupId());

		return studyGroupQuestionRepository
			.save(new StudyGroupQuestion(createDto.contents(), parent, member, studyGroup));
	}

	private StudyGroupQuestion findStudyGroupQuestionById(Long studyGroupQuestionId) {
		return studyGroupQuestionRepository.findFetchStudyGroupQuestionsById(studyGroupQuestionId)
			.orElseThrow(() -> StudyGroupException.notFoundStudyGroupQuestion(studyGroupQuestionId));
	}

	private Member findMemberById(Long memberId) {
		return memberRepository.findMemberById(memberId).orElseThrow(() -> MemberException.notFoundMember(memberId));
	}

	private StudyGroup findStudyGroupById(Long studyGroupId) {
		return studyGroupRepository.findStudyGroupById(studyGroupId)
			.orElseThrow(() -> StudyGroupException.notFoundStudyGroup(studyGroupId));
	}

	private void validateWriter(Member member, Long memberId) {
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
