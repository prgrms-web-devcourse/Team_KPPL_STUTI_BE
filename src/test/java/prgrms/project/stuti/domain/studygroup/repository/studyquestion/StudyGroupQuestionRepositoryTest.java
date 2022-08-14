package prgrms.project.stuti.domain.studygroup.repository.studyquestion;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import prgrms.project.stuti.config.RepositoryTestConfig;
import prgrms.project.stuti.domain.member.model.Mbti;
import prgrms.project.stuti.domain.studygroup.model.PreferredMbti;
import prgrms.project.stuti.domain.studygroup.model.Region;
import prgrms.project.stuti.domain.studygroup.model.StudyGroup;
import prgrms.project.stuti.domain.studygroup.model.StudyGroupQuestion;
import prgrms.project.stuti.domain.studygroup.model.StudyPeriod;
import prgrms.project.stuti.domain.studygroup.model.Topic;
import prgrms.project.stuti.domain.studygroup.repository.studygroup.StudyGroupRepository;
import prgrms.project.stuti.domain.studygroup.service.dto.StudyGroupQuestionDto;
import prgrms.project.stuti.domain.studygroup.service.response.StudyGroupQuestionsResponse;
import prgrms.project.stuti.global.page.PageResponse;

class StudyGroupQuestionRepositoryTest extends RepositoryTestConfig {

	@Autowired
	private StudyGroupRepository studyGroupRepository;

	@Autowired
	private StudyGroupQuestionRepository studyGroupQuestionRepository;

	private StudyGroup studyGroup;

	@BeforeEach
	void setup() {
		this.studyGroup = studyGroupRepository.save(
			StudyGroup
				.builder()
				.imageUrl("imageUrl")
				.title("test title")
				.topic(Topic.NETWORK)
				.isOnline(true)
				.region(Region.ONLINE)
				.numberOfRecruits(5)
				.preferredMBTIs(Set.of(new PreferredMbti(Mbti.ENFJ)))
				.studyPeriod(new StudyPeriod(LocalDateTime.now().plusDays(10), LocalDateTime.now().plusMonths(10)))
				.description("description")
				.build());
	}

	@Test
	@DisplayName("스터디 그룹 문의댓글을 아이디로 페치조인하여 조회한다.")
	void testFindFetchStudyGroupQuestionsById() {
		//given
		StudyGroupQuestion studyGroupQuestion = studyGroupQuestionRepository.save(saveParentQuestion());

		//when
		Optional<StudyGroupQuestion> retrievedStudyGroupQuestion =
			studyGroupQuestionRepository.findFetchStudyGroupQuestionsById(studyGroupQuestion.getId());

		//then
		assertThat(retrievedStudyGroupQuestion).isPresent();
		assertThat(retrievedStudyGroupQuestion.get().getId()).isEqualTo(studyGroupQuestion.getId());
		assertThat(retrievedStudyGroupQuestion.get().getStudyGroup().getId()).isEqualTo(studyGroup.getId());
	}

	@Test
	@DisplayName("부모댓글은 아이디 내림차순, 자식댓글은 아이디 오름차순으로 스터디 그룹 문의댓글 리스트를 페이징 처리하여 조회한다.")
	void testFindAllWithPagination() {
		//given
		for (int i = 0; i < 2; i++) {
			StudyGroupQuestion parentQuestion = saveParentQuestion();

			for (int j = 0; j < 4; j++) {
				saveChildrenQuestions(parentQuestion);
			}
		}

		StudyGroupQuestionDto.PageDto pageDto =
			new StudyGroupQuestionDto.PageDto(studyGroup.getId(), 10L, null);

		//when
		PageResponse<StudyGroupQuestionsResponse> pageResponses =
			studyGroupQuestionRepository.findAllWithPagination(pageDto);

		//then
		assertThat(pageResponses.contents()).isNotNull();
		assertThat(pageResponses.contents()).hasSize(2);
		assertThat(pageResponses.totalElements()).isEqualTo(2);
		assertThat(pageResponses.hasNext()).isFalse();

		StudyGroupQuestionsResponse content1 = pageResponses.contents().get(0);
		StudyGroupQuestionsResponse content2 = pageResponses.contents().get(1);

		assertThat(content1.studyGroupQuestionId()).isGreaterThan(content2.studyGroupQuestionId());
		assertThat(content1.children()).hasSize(4);
		assertThat(content1.children().get(0).studyGroupQuestionId())
			.isLessThan(content1.children().get(1).studyGroupQuestionId());
	}

	public StudyGroupQuestion saveParentQuestion() {
		return studyGroupQuestionRepository
			.save(new StudyGroupQuestion("parent question", null, member, studyGroup));
	}

	public void saveChildrenQuestions(StudyGroupQuestion parentQuestion) {
		studyGroupQuestionRepository
			.save(new StudyGroupQuestion("children question", parentQuestion, member, studyGroup));
	}
}
