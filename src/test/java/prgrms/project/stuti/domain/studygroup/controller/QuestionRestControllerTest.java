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
import static prgrms.project.stuti.domain.studygroup.controller.CommonStudyGroupTestUtils.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.request.ParameterDescriptor;
import org.springframework.test.web.servlet.ResultActions;

import prgrms.project.stuti.config.TestConfig;
import prgrms.project.stuti.domain.studygroup.controller.dto.QuestionCreateRequest;
import prgrms.project.stuti.domain.studygroup.controller.dto.QuestionUpdateRequest;
import prgrms.project.stuti.domain.studygroup.service.question.QuestionService;
import prgrms.project.stuti.domain.studygroup.service.response.QuestionIdResponse;

@WebMvcTest(controllers = QuestionRestController.class)
class QuestionRestControllerTest extends TestConfig {

	@MockBean
	private QuestionService questionService;

	private static final Long STUDY_GROUP_ID = 1L;

	private static final Long QUESTION_ID = 1L;

	private static final String QUESTION_API_PREFIX = "/api/v1/study-groups/{studyGroupId}/questions";

	@Test
	@DisplayName("문의 댓글을 생성한다.")
	void postQuestion() throws Exception {
		//given
		QuestionIdResponse idResponse = toIdResponse(QUESTION_ID);
		QuestionCreateRequest createRequest = new QuestionCreateRequest(null, "test content");
		String requestJsonString = objectMapper.writeValueAsString(createRequest);
		given(questionService.createQuestion(any())).willReturn(idResponse);

		//when
		ResultActions resultActions = mockMvc.perform(
			post(QUESTION_API_PREFIX, STUDY_GROUP_ID)
				.content(requestJsonString)
				.contentType(APPLICATION_JSON));

		//then
		resultActions
			.andExpectAll(
				status().isCreated(),
				content().json(objectMapper.writeValueAsString(idResponse)),
				content().contentType(APPLICATION_JSON))
			.andDo(document(COMMON_DOCS_NAME,
				requestHeaders(contentType(), host()),
				pathParameters(studyGroupIdPath()),
				requestFields(parentIdField(), questionContent()),
				responseHeaders(contentType(), contentLength(), location()),
				responseFields(questionIdField())));

	}

	@Test
	@DisplayName("문의 댓글을 수정한다.")
	void putQuestion() throws Exception {
		//given
		QuestionIdResponse idResponse = toIdResponse(QUESTION_ID);
		QuestionUpdateRequest updateRequest = new QuestionUpdateRequest("test content");
		given(questionService.updateQuestion(any())).willReturn(idResponse);

		//when
		ResultActions resultActions = mockMvc.perform(
			put(QUESTION_API_PREFIX + "/{questionId}", STUDY_GROUP_ID, QUESTION_ID)
				.content(objectMapper.writeValueAsString(updateRequest))
				.contentType(APPLICATION_JSON));

		//then
		resultActions
			.andExpectAll(
				status().isOk(),
				content().json(objectMapper.writeValueAsString(idResponse)),
				content().contentType(APPLICATION_JSON))
			.andDo(document(COMMON_DOCS_NAME,
				requestHeaders(contentType(), host()),
				pathParameters(studyGroupIdPath(), questionIdPath()),
				requestFields(questionContent()),
				responseHeaders(contentType(), contentLength()),
				responseFields(questionIdField())));
	}

	@Test
	@DisplayName("문의 댓글을 삭제한다.")
	void deleteQuestion() throws Exception {
		//given
		willDoNothing().given(questionService).deleteQuestion(any(), any(), any());

		//when
		ResultActions resultActions = mockMvc.perform(
			delete(QUESTION_API_PREFIX + "/{questionId}", STUDY_GROUP_ID, QUESTION_ID)
				.contentType(APPLICATION_JSON_VALUE));

		//then
		resultActions
			.andExpectAll(
				status().isOk(),
				content().contentType(APPLICATION_JSON))
			.andDo(document(COMMON_DOCS_NAME,
				requestHeaders(contentType(), host()),
				pathParameters(studyGroupIdPath(), questionIdPath()),
				responseHeaders(contentType())));
	}

	private QuestionIdResponse toIdResponse(Long questionId) {
		return new QuestionIdResponse(questionId);
	}

	private ParameterDescriptor questionIdPath() {
		return parameterWithName("questionId").description("문의댓글 아이디");
	}

	private FieldDescriptor questionContent() {
		return fieldWithPath("content").type(STRING).description("문의댓글 내용");
	}

	private FieldDescriptor parentIdField() {
		return fieldWithPath("parentId").type(NULL).description("상위 문의댓글 아이디");
	}

	private FieldDescriptor questionIdField() {
		return fieldWithPath("questionId").type(NUMBER).description("문의댓글 아이디");
	}
}
