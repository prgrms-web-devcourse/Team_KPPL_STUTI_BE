package prgrms.project.stuti.domain.studygroup.service;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import prgrms.project.stuti.config.ServiceTestConfig;
import prgrms.project.stuti.domain.member.model.Mbti;
import prgrms.project.stuti.domain.member.model.Member;
import prgrms.project.stuti.domain.studygroup.model.PreferredMbti;
import prgrms.project.stuti.domain.studygroup.model.Region;
import prgrms.project.stuti.domain.studygroup.model.StudyGroup;
import prgrms.project.stuti.domain.studygroup.model.StudyGroupQuestion;
import prgrms.project.stuti.domain.studygroup.model.StudyPeriod;
import prgrms.project.stuti.domain.studygroup.model.Topic;
import prgrms.project.stuti.domain.studygroup.repository.studygroup.StudyGroupRepository;
import prgrms.project.stuti.domain.studygroup.repository.studyquestion.StudyGroupQuestionRepository;
import prgrms.project.stuti.domain.studygroup.service.dto.StudyGroupQuestionDto;
import prgrms.project.stuti.domain.studygroup.service.response.StudyGroupQuestionResponse;
import prgrms.project.stuti.domain.studygroup.service.response.StudyGroupQuestionsResponse;
import prgrms.project.stuti.global.error.exception.StudyGroupException;
import prgrms.project.stuti.global.page.PageResponse;

class StudyGroupQuestionServiceTest extends ServiceTestConfig {

	@Autowired
	private StudyGroupQuestionService studyGroupQuestionService;

	@Autowired
	private StudyGroupRepository studyGroupRepository;

	@Autowired
	private StudyGroupQuestionRepository studyGroupQuestionRepository;

	private StudyGroup studyGroup;

	private final String errorMessageOfNotMatchWriter = "스터디 그룹 문의댓글의 작성자 정보와 일치하지 않습니다";

	@BeforeEach
	void setup() {
		studyGroup = saveStudyGroup();
	}

	@Nested
	@DisplayName("문의댓글 생성 기능은")
	class StudyGroupQuestionCreateTest {

		@Test
		@DisplayName("parentId 가 null 이면 부모댓글로 생성된다.")
		void testCreateStudyGroupQuestionAsParent() {
			//given
			StudyGroupQuestionDto.CreateDto createDto = toCreateDto(null);

			//when
			StudyGroupQuestionResponse parentQuestionResponse =
				studyGroupQuestionService.createStudyGroupQuestion(createDto);

			//then
			assertThat(parentQuestionResponse.parentId()).isNull();
			assertThat(parentQuestionResponse.contents()).isEqualTo(createDto.contents());
			assertThat(parentQuestionResponse.memberId()).isEqualTo(createDto.memberId());
		}

		@Nested
		@DisplayName("parentId 가 null 이 아니면")
		class ParentIdNotNullTest {

			@Test
			@DisplayName("parentId 로 문의댓글을 조회하여 해당 문의댓글의 자식댓글로 생성된다.")
			void testCreateStudyGroupQuestionAsChildren() {
				//given
				StudyGroupQuestion parentQuestion = saveStudyGroupQuestion(null);
				StudyGroupQuestionDto.CreateDto createDto = toCreateDto(parentQuestion.getId());

				//when
				StudyGroupQuestionResponse childrenQuestionResponse =
					studyGroupQuestionService.createStudyGroupQuestion(createDto);

				//then
				assertThat(childrenQuestionResponse.parentId()).isEqualTo(createDto.parentId());
				assertThat(childrenQuestionResponse.contents()).isEqualTo(createDto.contents());
				assertThat(childrenQuestionResponse.memberId()).isEqualTo(createDto.memberId());
			}

			@Test
			@DisplayName("parentId 로 문의댓글을 찾지 못하면 예외가 발생한다.")
			void testFailedToFindStudyGroupQuestionByParentId() {
				//given
				StudyGroupQuestionDto.CreateDto createDto = toCreateDto(-1L);

				//when
				assertThatThrownBy(() -> studyGroupQuestionService.createStudyGroupQuestion(createDto))
					.isInstanceOf(StudyGroupException.class)
					.hasMessageContaining("스터디 그룹 문의댓글을 찾을 수 없습니다");
			}
		}
	}

	@Nested
	@DisplayName("문의댓글 전체 조회 기능은")
	class StudyGroupQuestionGetTest {

		@Test
		@DisplayName("전체 문의댓글을 페이징 방식으로 조회한다.")
		void testGetStudyGroupQuestions() {
			//given
			int parentQuestionCount = 2;
			int childrenQuestionCount = 4;

			for (int i = 0; i < parentQuestionCount; i++) {
				StudyGroupQuestion parentQuestion = saveStudyGroupQuestion(null);

				for (int j = 0; j < childrenQuestionCount; j++) {
					saveStudyGroupQuestion(parentQuestion);
				}
			}

			StudyGroupQuestionDto.PageDto pageDto =
				new StudyGroupQuestionDto.PageDto(studyGroup.getId(), 5L, null);

			//when
			PageResponse<StudyGroupQuestionsResponse> pageResponses =
				studyGroupQuestionService.getStudyGroupQuestions(pageDto);

			List<StudyGroupQuestionsResponse> contents = pageResponses.contents();

			//then
			assertThat(pageResponses.totalElements()).isEqualTo(parentQuestionCount);
			assertThat(contents.get(0).children()).hasSize(childrenQuestionCount);
			assertThat(pageResponses.hasNext()).isFalse();
		}
	}

	@Nested
	@DisplayName("문의댓글 업데이트 기능은")
	class StudyGroupQuestionUpdateTest {

		String newContents = "new contents";

		@Test
		@DisplayName("요청자와 작성자의 정보가 같으면 업데이트한다.")
		void testUpdateStudyGroupQuestion() {
			//given
			StudyGroupQuestion questionBeforeUpdate = saveStudyGroupQuestion(null);
			String contentsBeforeUpdate = questionBeforeUpdate.getContents();
			StudyGroupQuestionDto.UpdateDto updateDto =
				toUpdateDto(questionBeforeUpdate.getId(), newContents, member);

			//when
			StudyGroupQuestionResponse questionResponseAfterUpdate =
				studyGroupQuestionService.updateStudyGroupQuestion(updateDto);

			//then
			assertThat(questionResponseAfterUpdate.studyGroupQuestionId()).isEqualTo(questionBeforeUpdate.getId());
			assertThat(contentsBeforeUpdate).isNotEqualTo(newContents);
			assertThat(questionResponseAfterUpdate.contents()).isEqualTo(newContents);
		}

		@Test
		@DisplayName("요청자와 작성자의 정보가 같지 않으면 예외가 발생한다.")
		void testFailedToUpdateStudyGroupQuestion() {
			//given
			StudyGroupQuestion newQuestion = saveStudyGroupQuestion(null);
			StudyGroupQuestionDto.UpdateDto updateDto = toUpdateDto(newQuestion.getId(), newContents, otherMember);

			//when, then
			assertThatThrownBy(() -> studyGroupQuestionService.updateStudyGroupQuestion(updateDto))
				.isInstanceOf(StudyGroupException.class)
				.hasMessageContaining(errorMessageOfNotMatchWriter);
		}
	}

	@Nested
	@DisplayName("문의댓글 삭제 기능은")
	class StudyGroupQuestionDeleteTest {

		@Test
		@DisplayName("요청자와 작성자의 정보가 같으면 삭제한다.")
		void testDeleteStudyGroupQuestion() {
			//given
			StudyGroupQuestion newQuestion = saveStudyGroupQuestion(null);
			StudyGroupQuestionDto.DeleteDto deleteDto = toDeleteDto(member, newQuestion.getId());

			//when
			StudyGroupQuestionResponse questionResponse = studyGroupQuestionService.deleteStudyGroupQuestion(
				deleteDto);
			Optional<StudyGroupQuestion> retrievedQuestion =
				studyGroupQuestionRepository.findById(questionResponse.studyGroupQuestionId());

			//then
			assertThat(questionResponse.studyGroupQuestionId()).isEqualTo(deleteDto.studyGroupQuestionId());
			assertThat(retrievedQuestion).isEmpty();
		}

		@Test
		@DisplayName("요청자와 작성자의 정보가 같지 않으면 예외가 발생한다.")
		void testFailedToDeleteStudyGroupQuestion() {
			//given
			StudyGroupQuestion newQuestion = saveStudyGroupQuestion(null);
			StudyGroupQuestionDto.DeleteDto deleteDto = toDeleteDto(otherMember, newQuestion.getId());

			//when, then
			assertThatThrownBy(() -> studyGroupQuestionService.deleteStudyGroupQuestion(deleteDto))
				.isInstanceOf(StudyGroupException.class)
				.hasMessageContaining(errorMessageOfNotMatchWriter);
		}
	}

	private StudyGroupQuestionDto.CreateDto toCreateDto(Long parentId) {
		return StudyGroupQuestionDto.CreateDto
			.builder()
			.parentId(parentId)
			.contents(createRandomContents())
			.studyGroupId(studyGroup.getId())
			.memberId(member.getId())
			.build();
	}

	private StudyGroupQuestionDto.UpdateDto toUpdateDto(Long studyGroupQuestionId, String newContents, Member member) {
		return StudyGroupQuestionDto.UpdateDto
			.builder()
			.studyGroupQuestionId(studyGroupQuestionId)
			.contents(newContents)
			.studyGroupId(studyGroup.getId())
			.memberId(member.getId())
			.build();
	}

	private StudyGroupQuestionDto.DeleteDto toDeleteDto(Member member, Long studyGroupQuestionId) {
		return new StudyGroupQuestionDto.DeleteDto(member.getId(), studyGroup.getId(), studyGroupQuestionId);
	}

	private StudyGroupQuestion saveStudyGroupQuestion(StudyGroupQuestion studyGroupQuestion) {
		return studyGroupQuestionRepository
			.save(new StudyGroupQuestion(createRandomContents(), studyGroupQuestion, member, studyGroup));
	}

	private String createRandomContents() {
		return RandomStringUtils.randomAlphabetic(5);
	}

	private StudyGroup saveStudyGroup() {
		return studyGroupRepository.save(
			StudyGroup
				.builder()
				.imageUrl("image")
				.title("title")
				.topic(Topic.AI)
				.isOnline(false)
				.region(Region.SEOUL)
				.preferredMBTIs(Set.of(new PreferredMbti(Mbti.ENFJ)))
				.numberOfRecruits(5)
				.studyPeriod(new StudyPeriod(LocalDateTime.now().plusDays(10), LocalDateTime.now().plusMonths(3)))
				.description("this is new study group")
				.build());
	}
}
