package prgrms.project.stuti.domain.studygroup.service;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import prgrms.project.stuti.config.ServiceTestConfig;
import prgrms.project.stuti.domain.studygroup.model.Region;
import prgrms.project.stuti.domain.studygroup.model.StudyGroup;
import prgrms.project.stuti.domain.studygroup.model.StudyGroupQuestion;
import prgrms.project.stuti.domain.studygroup.model.StudyPeriod;
import prgrms.project.stuti.domain.studygroup.model.Topic;
import prgrms.project.stuti.domain.studygroup.repository.StudyGroupQuestionRepository;
import prgrms.project.stuti.domain.studygroup.repository.studygroup.StudyGroupRepository;
import prgrms.project.stuti.domain.studygroup.service.dto.StudyGroupQuestionDto;
import prgrms.project.stuti.domain.studygroup.service.response.StudyGroupQuestionResponse;

class StudyGroupQuestionServiceTest extends ServiceTestConfig {

	@Autowired
	private StudyGroupQuestionService studyGroupQuestionService;

	@Autowired
	private StudyGroupRepository studyGroupRepository;

	@Autowired
	private StudyGroupQuestionRepository studyGroupQuestionRepository;

	private StudyGroup studyGroup;

	private StudyGroupQuestion studyGroupQuestion;

	@BeforeEach
	void setup() {
		this.studyGroup = studyGroupRepository.save(
			StudyGroup
				.builder()
				.imageUrl("image")
				.thumbnailUrl("thumbnail")
				.title("title")
				.topic(Topic.AI)
				.isOnline(false)
				.region(Region.SEOUL)
				.numberOfRecruits(5)
				.studyPeriod(new StudyPeriod(LocalDateTime.now().plusDays(10), LocalDateTime.now().plusMonths(3)))
				.description("this is new study group")
				.build());

		studyGroupQuestion =
			studyGroupQuestionRepository
				.save(new StudyGroupQuestion("test blabla", null, member, studyGroup));
	}

	@Test
	@DisplayName("스터디 그룹 문의댓글을 생성한다.")
	void testCreateStudyGroupQuestion() {
		//given
		Long studyGroupId = studyGroup.getId();
		Long memberId = member.getId();
		StudyGroupQuestionDto.CreateDto createDto =
			new StudyGroupQuestionDto.CreateDto(memberId, studyGroupId, null, "test contents");

		//when
		StudyGroupQuestionResponse questionResponse = studyGroupQuestionService.createStudyGroupQuestion(createDto);
		Optional<StudyGroupQuestion> optionalQuestion =
			studyGroupQuestionRepository.findById(questionResponse.studyGroupQuestionId());

		//then
		assertTrue(optionalQuestion.isPresent());
		assertEquals(questionResponse.studyGroupQuestionId(), optionalQuestion.get().getId());
	}

	@Test
	@DisplayName("스터디 문의댓글을 수정한다")
	void testUpdateStudyGroupQuestion() {
		//given
		Long studyGroupId = studyGroup.getId();
		Long memberId = member.getId();
		Long studyGroupQuestionId = studyGroupQuestion.getId();
		String newContent = "update blabla";
		StudyGroupQuestionDto.UpdateDto updateDto =
			new StudyGroupQuestionDto.UpdateDto(memberId, studyGroupQuestionId, studyGroupId, newContent);

		//when
		StudyGroupQuestionResponse questionResponse = studyGroupQuestionService.updateStudyGroupQuestion(updateDto);
		Optional<StudyGroupQuestion> optionalQuestion =
			studyGroupQuestionRepository.findById(questionResponse.studyGroupQuestionId());

		//then
		assertTrue(optionalQuestion.isPresent());
		assertEquals(newContent, optionalQuestion.get().getContents());
	}

	@Test
	@DisplayName("스터디 문의댓글을 삭제한다")
	void testDeleteQuestion() {
		//given
		Long studyGroupId = studyGroup.getId();
		Long memberId = member.getId();
		Long studyGroupQuestionId = studyGroupQuestion.getId();
		final StudyGroupQuestionDto.DeleteDto deleteDto =
			new StudyGroupQuestionDto.DeleteDto(memberId, studyGroupId, studyGroupQuestionId);

		//when
		Optional<StudyGroupQuestion> optionalQuestion = studyGroupQuestionRepository.findById(studyGroupQuestionId);
		studyGroupQuestionService.deleteStudyGroupQuestion(deleteDto);
		Optional<StudyGroupQuestion> retrievedQuestion = studyGroupQuestionRepository.findById(studyGroupQuestionId);

		//then
		assertTrue(optionalQuestion.isPresent());
		assertTrue(retrievedQuestion.isEmpty());
	}
}
