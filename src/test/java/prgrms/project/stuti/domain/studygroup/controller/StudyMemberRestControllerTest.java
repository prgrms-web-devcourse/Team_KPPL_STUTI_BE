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
import prgrms.project.stuti.domain.studygroup.service.response.StudyMemberIdResponse;
import prgrms.project.stuti.domain.studygroup.service.studymember.StudyMemberService;

@WebMvcTest(controllers = StudyMemberRestController.class)
class StudyMemberRestControllerTest extends TestConfig {

	@MockBean
	private StudyMemberService studyMemberService;

	private final Long studyGroupId = 1L;
	private final Long studyMemberId = 1L;

	@Test
	@DisplayName("스터디 그룹에 가입신청을한다.")
	void postApplyStudyGroup() throws Exception {
		//given
		StudyMemberIdResponse idResponse = new StudyMemberIdResponse(1L);
		given(studyMemberService.applyForJoinStudyGroup(any(), any())).willReturn(idResponse);

		//when
		ResultActions resultActions = mockMvc.perform(
			post("/api/v1/study-groups/{studyGroupId}/study-members",
				studyGroupId).contentType(APPLICATION_JSON));

		resultActions
			.andExpectAll(
				status().isOk(),
				content().json(objectMapper.writeValueAsString(idResponse)))
			.andDo(
				document(COMMON_DOCS_NAME,
					requestHeaders(contentType(), host()),
					pathParameters(studyGroupIdPath()),
					responseHeaders(contentType()),
					responseFields(studyMemberIdField())));
	}

	@Test
	@DisplayName("스터디 가입신청을 수락한다.")
	void acceptRequestForJoin() throws Exception {
		//given
		StudyMemberIdResponse idResponse = toStudyMemberIdResponse(studyMemberId);
		given(studyMemberService.acceptRequestForJoin(any(), any(), any())).willReturn(idResponse);

		//when
		ResultActions resultActions = mockMvc.perform(
			patch("/api/v1/study-groups/{studyGroupId}/study-members/{studyMemberId}",
				studyGroupId, studyMemberId)
				.contentType(APPLICATION_JSON));

		//then
		resultActions
			.andExpectAll(
				status().isOk(),
				content().json(objectMapper.writeValueAsString(idResponse)),
				content().contentType(APPLICATION_JSON))
			.andDo(document(COMMON_DOCS_NAME,
				requestHeaders(contentType(), host()),
				pathParameters(studyGroupIdPath(), studyMemberIdPath()),
				responseHeaders(contentType(), contentLength()),
				responseFields(studyMemberIdField())));
	}

	@Test
	@DisplayName("스터디 멤버를 삭제한다.")
	void deleteStudyMember() throws Exception {
		//given
		willDoNothing().given(studyMemberService).deleteStudyMember(any(), any(), any());

		//when
		ResultActions resultActions = mockMvc.perform(
			delete("/api/v1/study-groups/{studyGroupId}/study-members/{studyMemberId}",
				studyGroupId, studyMemberId).contentType(APPLICATION_JSON_VALUE));

		//then
		resultActions
			.andExpectAll(
				status().isOk(),
				content().contentType(APPLICATION_JSON))
			.andDo(document(COMMON_DOCS_NAME,
				requestHeaders(contentType(), host()),
				pathParameters(studyGroupIdPath(), studyMemberIdPath()),
				responseHeaders(contentType())));
	}

	private StudyMemberIdResponse toStudyMemberIdResponse(Long studyMemberId) {
		return new StudyMemberIdResponse(studyMemberId);
	}

	private ParameterDescriptor studyMemberIdPath() {
		return parameterWithName(STUDY_MEMBER_ID.value()).description("스터디 멤버 아이디");
	}

	private FieldDescriptor studyMemberIdField() {
		return fieldWithPath(STUDY_MEMBER_ID.value()).type(NUMBER).description("스터디 멤버 아이디");
	}
}
