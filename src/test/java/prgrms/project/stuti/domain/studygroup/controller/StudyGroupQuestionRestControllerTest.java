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
import static prgrms.project.stuti.domain.studygroup.controller.CommonStudyGroupTestUtils.CommonField.*;
import static prgrms.project.stuti.domain.studygroup.controller.CommonStudyGroupTestUtils.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.request.ParameterDescriptor;
import org.springframework.test.web.servlet.ResultActions;

import prgrms.project.stuti.config.TestConfig;
import prgrms.project.stuti.domain.studygroup.controller.dto.StudyGroupQuestionCreateRequest;
import prgrms.project.stuti.domain.studygroup.controller.dto.StudyGroupQuestionUpdateRequest;
import prgrms.project.stuti.domain.studygroup.service.StudyGroupQuestionService;
import prgrms.project.stuti.domain.studygroup.service.response.StudyGroupQuestionIdResponse;

@WebMvcTest(controllers = StudyGroupQuestionRestController.class)
class StudyGroupQuestionRestControllerTest extends TestConfig {

	@MockBean
	private StudyGroupQuestionService studyGroupQuestionService;

	private final Long studyGroupId = 1L;

	private final Long studyGroupQuestionId = 1L;

	private final String studyGroupQuestionApiPrefix = "/api/v1/study-groups/{studyGroupId}/questions";

	@Test
	@DisplayName("스터디 그룹 문의 댓글을 생성한다.")
	void postStudyGroupQuestion() throws Exception {
		//given
		StudyGroupQuestionIdResponse idResponse = toIdResponse(studyGroupQuestionId);
		StudyGroupQuestionCreateRequest createRequest =
			new StudyGroupQuestionCreateRequest(null, "test contents");
		String requestJsonString = objectMapper.writeValueAsString(createRequest);
		given(studyGroupQuestionService.createStudyGroupQuestion(any())).willReturn(idResponse);

		//when
		ResultActions resultActions = mockMvc.perform(
			post(studyGroupQuestionApiPrefix, studyGroupId)
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
				requestFields(parentIdField(), contents()),
				responseHeaders(contentType(), contentLength(), location()),
				responseFields(studyGroupQuestionIdField())));

	}

	@Test
	@DisplayName("스터디 그룹 문의 댓글을 수정한다.")
	void putStudyGroupQuestion() throws Exception {
		//given
		StudyGroupQuestionIdResponse idResponse = toIdResponse(studyGroupQuestionId);
		StudyGroupQuestionUpdateRequest updateRequest = new StudyGroupQuestionUpdateRequest("test contents");
		given(studyGroupQuestionService.updateStudyGroupQuestion(any())).willReturn(idResponse);

		//when
		ResultActions resultActions = mockMvc.perform(
			put(studyGroupQuestionApiPrefix + "/{studyGroupQuestionId}", studyGroupId, studyGroupQuestionId)
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
				pathParameters(studyGroupIdPath(), studyGroupQuestionIdPath()),
				requestFields(contents()),
				responseHeaders(contentType(), contentLength()),
				responseFields(studyGroupQuestionIdField())));
	}

	@Test
	@DisplayName("스터디 그룹 문의 댓글을 삭제한다.")
	void deleteStudyGroupQuestion() throws Exception {
		//given
		willDoNothing().given(studyGroupQuestionService).deleteStudyGroupQuestion(any(), any(), any());

		//when
		ResultActions resultActions = mockMvc.perform(
			delete(studyGroupQuestionApiPrefix + "/{studyGroupQuestionId}", studyGroupId, studyGroupQuestionId)
				.contentType(APPLICATION_JSON_VALUE));

		//then
		resultActions
			.andExpectAll(
				status().isOk(),
				content().contentType(APPLICATION_JSON))
			.andDo(document(COMMON_DOCS_NAME,
				requestHeaders(contentType(), host()),
				pathParameters(studyGroupIdPath(), studyGroupQuestionIdPath()),
				responseHeaders(contentType())));
	}

	private StudyGroupQuestionIdResponse toIdResponse(Long studyGroupQuestionId) {
		return new StudyGroupQuestionIdResponse(studyGroupQuestionId);
	}

	private ParameterDescriptor studyGroupQuestionIdPath() {
		return parameterWithName(STUDY_GROUP_QUESTION_ID.value()).description("스터디 그룹 문의댓글 아이디");
	}

	private FieldDescriptor contents() {
		return fieldWithPath("contents").type(STRING).description("문의댓글 내용");
	}

	private FieldDescriptor parentIdField() {
		return fieldWithPath("parentId").type(NULL).description("상위 문의댓글 아이디");
	}

	private FieldDescriptor studyGroupQuestionIdField() {
		return fieldWithPath(STUDY_GROUP_QUESTION_ID.value()).type(NUMBER).description("스터디 그룹 문의댓글 아이디");
	}
}
