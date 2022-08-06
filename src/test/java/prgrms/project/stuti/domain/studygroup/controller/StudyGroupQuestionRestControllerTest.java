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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import prgrms.project.stuti.config.TestConfig;
import prgrms.project.stuti.domain.studygroup.controller.dto.StudyGroupQuestionCreateRequest;
import prgrms.project.stuti.domain.studygroup.controller.dto.StudyGroupQuestionUpdateRequest;
import prgrms.project.stuti.domain.studygroup.service.StudyGroupQuestionService;
import prgrms.project.stuti.domain.studygroup.service.response.StudyGroupQuestionListResponse;
import prgrms.project.stuti.domain.studygroup.service.response.StudyGroupQuestionResponse;
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
	@DisplayName("스터디 그룹 문의 댓글 페이징 조회한다.")
	void getStudyGroupQuestions() throws Exception {
		//given
		PageResponse<StudyGroupQuestionListResponse> pageResponse = toListResponse();
		given(studyGroupQuestionService.getStudyGroupQuestions(any(), any(), any())).willReturn(pageResponse);

		//when
		ResultActions resultActions = mockMvc.perform(
			get(studyGroupQuestionApiPrefix, studyGroupId)
				.queryParams(toPageRequestParams())
				.contentType(APPLICATION_JSON));

		//then
		resultActions
			.andExpectAll(
				status().isOk(),
				content().json(objectMapper.writeValueAsString(pageResponse)),
				content().contentType(APPLICATION_JSON))
			.andDo(document(COMMON_DOCS_NAME,
				requestHeaders(contentType(), host()),
				pathParameters(studyGroupIdPath()),
				requestParameters(pageRequestParameters()),
				responseHeaders(contentType(), contentLength()),
				responseFields(pageResponseFields())));

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

	private PageResponse<StudyGroupQuestionListResponse> toListResponse() {
		final List<StudyGroupQuestionListResponse> contents = List.of(StudyGroupQuestionListResponse
			.builder()
			.studyGroupQuestionId(studyGroupQuestionId)
			.memberId(1L)
			.parentId(1L)
			.profileImageUrl("test profile image url")
			.nickname("test nickname")
			.contents("test contents")
			.updatedAt(LocalDateTime.now())
			.children(List.of(toQuestionResponse()))
			.build());

		return new PageResponse<>(contents, false, 10);
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

	private MultiValueMap<String, String> toPageRequestParams() {
		MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
		map.add("size", "10");
		map.add("lastStudyGroupQuestionId", "1");

		return map;
	}

	private List<ParameterDescriptor> pageRequestParameters() {
		return List.of(
			parameterWithName("size").description("페이지 사이즈"),
			parameterWithName("lastStudyGroupQuestionId").description("마지막으로 본 스터디 그룹 문의댓글 아이디")
		);
	}

	private List<FieldDescriptor> pageResponseFields() {
		return List.of(
			subsectionWithPath("contents").type(ARRAY).description("페이징 컨텐츠"),
			fieldWithPath("hasNext").type(BOOLEAN).description("다음 컨텐츠 유무"),
			fieldWithPath("totalElements").type(NUMBER).description("총 컨텐츠 수")
		);
	}
}
