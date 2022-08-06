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

import java.time.LocalDateTime;
import java.util.List;

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
import prgrms.project.stuti.domain.studygroup.service.response.StudyGroupQuestionResponse;

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
		StudyGroupQuestionResponse questionResponse = toQuestionResponse();
		StudyGroupQuestionCreateRequest createRequest =
			new StudyGroupQuestionCreateRequest(null, "test contents");
		String requestJsonString = objectMapper.writeValueAsString(createRequest);
		given(studyGroupQuestionService.createStudyGroupQuestion(any())).willReturn(questionResponse);

		//when
		ResultActions resultActions = mockMvc.perform(
			post(studyGroupQuestionApiPrefix, studyGroupId)
				.content(requestJsonString)
				.contentType(APPLICATION_JSON));

		//then
		resultActions
			.andExpectAll(
				status().isCreated(),
				content().json(objectMapper.writeValueAsString(questionResponse)),
				content().contentType(APPLICATION_JSON))
			.andDo(document(COMMON_DOCS_NAME,
				requestHeaders(contentType(), host()),
				pathParameters(studyGroupIdPath()),
				requestFields(parentIdField(), contents()),
				responseHeaders(contentType(), contentLength(), location()),
				responseFields(commonQuestionResponse())));

	}

	@Test
	@DisplayName("스터디 그룹 문의 댓글을 수정한다.")
	void patchStudyGroupQuestion() throws Exception {
		//given
		StudyGroupQuestionResponse questionResponse = toQuestionResponse();
		StudyGroupQuestionUpdateRequest updateRequest = new StudyGroupQuestionUpdateRequest("test contents");
		given(studyGroupQuestionService.updateStudyGroupQuestion(any())).willReturn(questionResponse);

		//when
		ResultActions resultActions = mockMvc.perform(
			patch(studyGroupQuestionApiPrefix + "/{studyGroupQuestionId}", studyGroupId, studyGroupQuestionId)
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
				responseFields(commonQuestionResponse())));
	}

	@Test
	@DisplayName("스터디 그룹 문의 댓글을 삭제한다.")
	void deleteStudyGroupQuestion() throws Exception {
		//given
		StudyGroupQuestionResponse questionResponse = toQuestionResponse();
		given(studyGroupQuestionService.deleteStudyGroupQuestion(any(), any(), any())).willReturn(questionResponse);

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
				responseHeaders(contentType()),
				responseFields(commonQuestionResponse())));
	}

	private StudyGroupQuestionResponse toQuestionResponse() {
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

	private ParameterDescriptor studyGroupQuestionIdPath() {
		return parameterWithName(STUDY_GROUP_QUESTION_ID.value()).description("스터디 그룹 문의댓글 아이디");
	}

	private FieldDescriptor contents() {
		return fieldWithPath("contents").type(STRING).description("문의댓글 내용");
	}

	private FieldDescriptor parentIdField() {
		return fieldWithPath("parentId").type(NULL).description("상위 문의댓글 아이디");
	}

	private List<FieldDescriptor> commonQuestionResponse() {
		return List.of(
			fieldWithPath(STUDY_GROUP_QUESTION_ID.value()).type(NUMBER).description("스터디 그룹 문의댓글 아이디"),
			parentIdField(),
			fieldWithPath(PROFILE_IMAGE_URL.value()).type(STRING).description("프로필 이미지 url"),
			fieldWithPath(MEMBER_ID.value()).type(NUMBER).description("회원 아이디"),
			fieldWithPath(NICKNAME.value()).type(STRING).description("닉네임"),
			contents(),
			fieldWithPath("updatedAt").type(STRING).description("업데이트 시간")
		);
	}
}
