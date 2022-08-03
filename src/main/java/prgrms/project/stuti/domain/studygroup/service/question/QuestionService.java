package prgrms.project.stuti.domain.studygroup.service.question;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import prgrms.project.stuti.domain.member.model.Member;
import prgrms.project.stuti.domain.member.repository.MemberRepository;
import prgrms.project.stuti.domain.studygroup.model.Question;
import prgrms.project.stuti.domain.studygroup.model.StudyGroup;
import prgrms.project.stuti.domain.studygroup.repository.QuestionRepository;
import prgrms.project.stuti.domain.studygroup.repository.studygroup.StudyGroupRepository;
import prgrms.project.stuti.domain.studygroup.service.dto.QuestionCreateDto;
import prgrms.project.stuti.domain.studygroup.service.response.QuestionIdResponse;
import prgrms.project.stuti.global.error.exception.MemberException;
import prgrms.project.stuti.global.error.exception.StudyGroupException;

@Service
@RequiredArgsConstructor
public class QuestionService {

	private final MemberRepository memberRepository;
	private final QuestionRepository questionRepository;
	private final StudyGroupRepository studyGroupRepository;

	@Transactional
	public QuestionIdResponse createQuestion(QuestionCreateDto createDto) {
		Long parentId = createDto.parentId();
		Question question = saveQuestion(createDto, parentId);

		return QuestionConverter.toQuestionIdResponse(question.getId());
	}

	private Question saveQuestion(QuestionCreateDto createDto, Long parentId) {
		Question parent = parentId == null
			? null
			: questionRepository.findById(parentId).orElseThrow(() -> StudyGroupException.notFoundQuestion(parentId));
		Member member = findMember(createDto.memberId());
		StudyGroup studyGroup = findStudyGroup(createDto.studyGroupId());

		return questionRepository.save(new Question(createDto.content(), parent, member, studyGroup));
	}

	private Member findMember(Long memberId) {
		return memberRepository.findMemberById(memberId).orElseThrow(() -> MemberException.notFoundMember(memberId));
	}

	private StudyGroup findStudyGroup(Long studyGroupId) {
		return studyGroupRepository.findStudyGroupById(studyGroupId)
			.orElseThrow(() -> StudyGroupException.notFoundStudyGroup(studyGroupId));
	}
}
