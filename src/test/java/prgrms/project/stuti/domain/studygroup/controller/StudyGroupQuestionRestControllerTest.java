package prgrms.project.stuti.domain.studygroup.controller;

import static org.mockito.BDDMockito.*;
import static org.springframework.http.MediaType.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static prgrms.project.stuti.domain.studygroup.controller.StudyGroupTestUtils.CommonField.*;
import static prgrms.project.stuti.domain.studygroup.controller.StudyGroupTestUtils.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.request.ParameterDescriptor;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import prgrms.project.stuti.config.TestConfig;
import prgrms.project.stuti.domain.studygroup.controller.dto.StudyGroupQuestionRequest;
import prgrms.project.stuti.domain.studygroup.service.StudyGroupQuestionService;
import prgrms.project.stuti.domain.studygroup.service.response.StudyGroupQuestionResponse;
import prgrms.project.stuti.domain.studygroup.service.response.StudyGroupQuestionsResponse;
import prgrms.project.stuti.global.page.PageResponse;

@WebMvcTest(controllers = StudyGroupQuestionRestController.class)
class StudyGroupQuestionRestControllerTest extends TestConfig {

	@MockBean
	private StudyGroupQuestionService studyGroupQuestionService;

	private final Long studyGroupId = 1L;

	private final Long studyGroupQuestionId = 1L;

	private final String studyGroupQuestionApiPrefix = "/api/v1/study-groups/{studyGroupId}/questions";

	@Test
	@DisplayName("스터디 그룹 문의 댓글을 생성한다.")
	void createStudyGroupQuestion() throws Exception {
		//given
		StudyGroupQuestionResponse questionResponse = toStudyGroupQuestionResponse();
		StudyGroupQuestionRequest.CreateRequest createRequest =
			new StudyGroupQuestionRequest.CreateRequest(null, "test contents");
		String jsonRequestString = objectMapper.writeValueAsString(createRequest);
		given(studyGroupQuestionService.createStudyGroupQuestion(any())).willReturn(questionResponse);

		//when
		ResultActions resultActions = mockMvc.perform(post(studyGroupQuestionApiPrefix, studyGroupId)
			.content(jsonRequestString)
			.contentType(APPLICATION_JSON));

		//then
		resultActions
			.andExpectAll(
				status().isOk(),
				content().json(objectMapper.writeValueAsString(questionResponse)),
				content().contentType(APPLICATION_JSON))
			.andDo(document(COMMON_DOCS_NAME,
				requestHeaders(contentType(), host()),
				pathParameters(studyGroupIdPath()),
				requestFields(parentIdField(), contents()),
				responseHeaders(contentType(), contentLength()),
				responseFields(studyGroupQuestionFields())));
	}

	@Test
	@DisplayName("스터디 그룹 문의 댓글을 페이징 조회한다.")
	void getStudyGroupQuestions() throws Exception {
		//given
		PageResponse<StudyGroupQuestionsResponse> questionsResponse = toStudyGroupQuestionsResponse();
		given(studyGroupQuestionService.getStudyGroupQuestions(any())).willReturn(questionsResponse);

		//when
		ResultActions resultActions = mockMvc.perform(get(studyGroupQuestionApiPrefix, studyGroupId)
			.queryParams(toStudyGroupQuestionPageParams())
			.contentType(APPLICATION_JSON));

		//then
		resultActions
			.andExpectAll(
				status().isOk(),
				content().json(objectMapper.writeValueAsString(questionsResponse)),
				content().contentType(APPLICATION_JSON))
			.andDo(document(COMMON_DOCS_NAME,
				requestHeaders(contentType(), host()),
				pathParameters(studyGroupIdPath()),
				requestParameters(pageRequestParams()),
				responseHeaders(contentType(), contentLength()),
				responseFields(studyGroupQuestionsFields())));
	}

	@Test
	@DisplayName("스터디 그룹 문의 댓글을 수정한다.")
	void updateStudyGroupQuestion() throws Exception {
		//given
		StudyGroupQuestionResponse questionResponse = toStudyGroupQuestionResponse();
		StudyGroupQuestionRequest.UpdateRequest updateRequest =
			new StudyGroupQuestionRequest.UpdateRequest("test contents");
		given(studyGroupQuestionService.updateStudyGroupQuestion(any())).willReturn(questionResponse);

		//when
		ResultActions resultActions = mockMvc.perform(
			patch(studyGroupQuestionApiPrefix + "/{studyGroupQuestionId}",
				studyGroupId, studyGroupQuestionId)
				.content(objectMapper.writeValueAsString(updateRequest))
				.contentType(APPLICATION_JSON));

		//then
		resultActions
			.andExpectAll(
				status().isOk(),
				content().json(objectMapper.writeValueAsString(questionResponse)),
				content().contentType(APPLICATION_JSON))
			.andDo(document(COMMON_DOCS_NAME,
				requestHeaders(contentType(), host()),
				pathParameters(studyGroupIdPath(), studyGroupQuestionIdPath()),
				requestFields(contents()),
				responseHeaders(contentType(), contentLength()),
				responseFields(studyGroupQuestionFields())));
	}

	@Test
	@DisplayName("스터디 그룹 문의 댓글을 삭제한다.")
	void deleteStudyGroupQuestion() throws Exception {
		//given
		StudyGroupQuestionResponse questionResponse = toStudyGroupQuestionResponse();
		given(studyGroupQuestionService.deleteStudyGroupQuestion(any())).willReturn(questionResponse);

		//when
		ResultActions resultActions = mockMvc.perform(
			delete(studyGroupQuestionApiPrefix + "/{studyGroupQuestionId}",
				studyGroupId, studyGroupQuestionId)
				.contentType(APPLICATION_JSON_VALUE));

		//then
		resultActions
			.andExpectAll(
				status().isOk(),
				content().contentType(APPLICATION_JSON))
			.andDo(document(COMMON_DOCS_NAME,
				requestHeaders(contentType(), host()),
				pathParameters(studyGroupIdPath(), studyGroupQuestionIdPath()),
				responseHeaders(contentType()),
				responseFields(studyGroupQuestionFields())));
	}

	private StudyGroupQuestionResponse toStudyGroupQuestionResponse() {
		return StudyGroupQuestionResponse
			.builder()
			.studyGroupQuestionId(1L)
			.parentId(null)
			.profileImageUrl("profile image url")
			.memberId(1L)
			.nickname("test nickname")
			.contents("test")
			.updatedAt(LocalDateTime.now())
			.build();
	}

	private PageResponse<StudyGroupQuestionsResponse> toStudyGroupQuestionsResponse() {
		final List<StudyGroupQuestionsResponse> contents =
			List.of(
				StudyGroupQuestionsResponse
					.builder()
					.studyGroupQuestionId(studyGroupQuestionId)
					.memberId(1L)
					.parentId(1L)
					.profileImageUrl("test profile image url")
					.nickname("test nickname")
					.contents("test contents")
					.updatedAt(LocalDateTime.now().minusDays(10))
					.children(List.of(
						toStudyGroupQuestionResponse(),
						toStudyGroupQuestionResponse(),
						toStudyGroupQuestionResponse()))
					.build());

		return new PageResponse<>(contents, false, 10);
	}

	private ParameterDescriptor studyGroupQuestionIdPath() {
		return parameterWithName(STUDY_GROUP_QUESTION_ID.field()).description(STUDY_GROUP_QUESTION_ID.description());
	}

	private FieldDescriptor contents() {
		return fieldWithPath("contents").type(STRING).description("문의댓글 내용");
	}

	private FieldDescriptor parentIdField() {
		return fieldWithPath("parentId").type(NULL).description("상위 문의댓글 아이디");
	}

	private List<FieldDescriptor> studyGroupQuestionFields() {
		return List.of(
			fieldWithPath(STUDY_GROUP_QUESTION_ID.field()).type(NUMBER)
				.description(STUDY_GROUP_QUESTION_ID.description()),
			parentIdField(),
			fieldWithPath(PROFILE_IMAGE_URL.field()).type(STRING).description(PROFILE_IMAGE_URL.description()),
			fieldWithPath(MEMBER_ID.field()).type(NUMBER).description(MEMBER_ID.description()),
			fieldWithPath(NICKNAME.field()).type(STRING).description(NICKNAME.description()),
			contents(),
			fieldWithPath("updatedAt").type(STRING).description("마지막으로 수정된 시간")
		);
	}

	private MultiValueMap<String, String> toStudyGroupQuestionPageParams() {
		MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
		map.add(SIZE.field(), "10");
		map.add(LAST_STUDY_GROUP_QUESTION_ID.field(), "1");

		return map;
	}

	private List<ParameterDescriptor> pageRequestParams() {
		return List.of(
			parameterWithName(SIZE.field()).description(SIZE.description()),
			parameterWithName(LAST_STUDY_GROUP_QUESTION_ID.field())
				.description(LAST_STUDY_GROUP_QUESTION_ID.description()));
	}

	private List<FieldDescriptor> studyGroupQuestionsFields() {
		return List.of(
			subsectionWithPath(PAGE_CONTENTS.field()).type(ARRAY).description(PAGE_CONTENTS.description()),
			fieldWithPath(HAS_NEXT.field()).type(BOOLEAN).description(HAS_NEXT.description()),
			fieldWithPath("totalElements").type(NUMBER).description("총 컨텐츠 수"));
	}
}
