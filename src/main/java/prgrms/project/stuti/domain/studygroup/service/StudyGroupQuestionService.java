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
import prgrms.project.stuti.domain.studygroup.service.dto.StudyGroupQuestionCreateDto;
import prgrms.project.stuti.domain.studygroup.service.dto.StudyGroupQuestionUpdateDto;
import prgrms.project.stuti.domain.studygroup.service.response.StudyGroupQuestionIdResponse;
import prgrms.project.stuti.global.error.exception.MemberException;
import prgrms.project.stuti.global.error.exception.StudyGroupException;

@Service
@RequiredArgsConstructor
public class StudyGroupQuestionService {

	private final MemberRepository memberRepository;
	private final StudyGroupRepository studyGroupRepository;
	private final StudyGroupQuestionRepository studyGroupQuestionRepository;

	@Transactional
	public StudyGroupQuestionIdResponse createStudyGroupQuestion(StudyGroupQuestionCreateDto createDto) {
		Long parentId = createDto.parentId();
		StudyGroupQuestion studyGroupQuestion = saveStudyGroupQuestion(createDto, parentId);

		return StudyGroupConverter.toStudyGroupQuestionIdResponse(studyGroupQuestion.getId());
	}

	@Transactional
	public StudyGroupQuestionIdResponse updateStudyGroupQuestion(StudyGroupQuestionUpdateDto updateDto) {
		StudyGroupQuestion studyGroupQuestion = findStudyGroupQuestion(updateDto.studyGroupQuestionId());
		studyGroupQuestion.updateContents(updateDto.contents());

		return StudyGroupConverter.toStudyGroupQuestionIdResponse(studyGroupQuestion.getId());
	}

	@Transactional
	public void deleteStudyGroupQuestion(Long memberId, Long studyGroupId, Long studyGroupQuestionId) {
		StudyGroupQuestion studyGroupQuestion = studyGroupQuestionRepository.findStudyGroupQuestionById(
			studyGroupQuestionId);
		Member member = studyGroupQuestion.getMember();
		StudyGroup studyGroup = studyGroupQuestion.getStudyGroup();

		if (member.getId().equals(memberId) && studyGroup.getId().equals(studyGroupId)) {
			studyGroupQuestionRepository.delete(studyGroupQuestion);
		}
	}

	private StudyGroupQuestion saveStudyGroupQuestion(StudyGroupQuestionCreateDto createDto, Long parentId) {
		StudyGroupQuestion parent = parentId == null
			? null
			: findStudyGroupQuestion(parentId);
		Member member = findMember(createDto.memberId());
		StudyGroup studyGroup = findStudyGroup(createDto.studyGroupId());

		return studyGroupQuestionRepository.save(
			new StudyGroupQuestion(createDto.contents(), parent, member, studyGroup));
	}

	private StudyGroupQuestion findStudyGroupQuestion(Long studyGroupQuestionId) {
		return studyGroupQuestionRepository.findById(studyGroupQuestionId)
			.orElseThrow(() -> StudyGroupException.notFoundStudyGroupQuestion(studyGroupQuestionId));
	}

	private Member findMember(Long memberId) {
		return memberRepository.findMemberById(memberId).orElseThrow(() -> MemberException.notFoundMember(memberId));
	}

	private StudyGroup findStudyGroup(Long studyGroupId) {
		return studyGroupRepository.findStudyGroupById(studyGroupId)
			.orElseThrow(() -> StudyGroupException.notFoundStudyGroup(studyGroupId));
	}
}
