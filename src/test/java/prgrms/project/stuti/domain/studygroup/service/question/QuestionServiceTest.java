package prgrms.project.stuti.domain.studygroup.service.question;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import prgrms.project.stuti.config.ServiceTestConfig;
import prgrms.project.stuti.domain.studygroup.model.Question;
import prgrms.project.stuti.domain.studygroup.model.Region;
import prgrms.project.stuti.domain.studygroup.model.StudyGroup;
import prgrms.project.stuti.domain.studygroup.model.StudyPeriod;
import prgrms.project.stuti.domain.studygroup.model.Topic;
import prgrms.project.stuti.domain.studygroup.repository.QuestionRepository;
import prgrms.project.stuti.domain.studygroup.repository.studygroup.StudyGroupRepository;
import prgrms.project.stuti.domain.studygroup.service.dto.QuestionCreateDto;
import prgrms.project.stuti.domain.studygroup.service.dto.QuestionUpdateDto;
import prgrms.project.stuti.domain.studygroup.service.response.QuestionIdResponse;

class QuestionServiceTest extends ServiceTestConfig {

	@Autowired
	private QuestionService questionService;

	@Autowired
	private StudyGroupRepository studyGroupRepository;

	@Autowired
	private QuestionRepository questionRepository;

	private StudyGroup studyGroup;

	private Question question;

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

		question = questionRepository.save(new Question("test blabla", null, member, studyGroup));
	}

	@Test
	@DisplayName("스터디 문의댓글을 생성한다.")
	void testCreateQuestion() {
		//given
		Long studyGroupId = studyGroup.getId();
		Long memberId = member.getId();
		QuestionCreateDto createDto = new QuestionCreateDto(memberId, studyGroupId, null, "test content");

		//when
		QuestionIdResponse idResponse = questionService.createQuestion(createDto);
		Optional<Question> optionalQuestion = questionRepository.findById(idResponse.questionId());

		//then
		assertTrue(optionalQuestion.isPresent());
		assertEquals(idResponse.questionId(), optionalQuestion.get().getId());
	}

	@Test
	@DisplayName("스터디 문의댓글을 수정한다")
	void testUpdateQuestion() {
		//given
		Long studyGroupId = studyGroup.getId();
		Long memberId = member.getId();
		Long questionId = question.getId();
		String newContent = "update blabla";
		QuestionUpdateDto updateDto = new QuestionUpdateDto(memberId, questionId, studyGroupId, newContent);

		//when
		QuestionIdResponse idResponse = questionService.updateQuestion(updateDto);
		Optional<Question> optionalQuestion = questionRepository.findById(idResponse.questionId());

		//then
		assertTrue(optionalQuestion.isPresent());
		assertEquals(newContent, optionalQuestion.get().getContent());
	}

	@Test
	@DisplayName("스터디 문의댓글을 삭제한다")
	void testDeleteQuestion() {
		//given
		Long studyGroupId = studyGroup.getId();
		Long memberId = member.getId();
		Long questionId = question.getId();

		//when
		Optional<Question> optionalQuestion = questionRepository.findById(questionId);
		questionService.deleteQuestion(memberId, studyGroupId, questionId);
		Optional<Question> retrievedQuestion = questionRepository.findById(questionId);

		//then
		assertTrue(optionalQuestion.isPresent());
		assertTrue(retrievedQuestion.isEmpty());
	}
}
