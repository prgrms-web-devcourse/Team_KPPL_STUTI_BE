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
import prgrms.project.stuti.domain.studygroup.service.StudyGroupMemberService;
import prgrms.project.stuti.domain.studygroup.service.response.StudyGroupMemberIdResponse;

@WebMvcTest(controllers = StudyGroupMemberRestController.class)
class StudyGroupMemberRestControllerTest extends TestConfig {

	@MockBean
	private StudyGroupMemberService studyGroupMemberService;

	private final Long studyGroupId = 1L;

	private final Long studyGroupMemberId = 1L;

	private final String studyGroupMemberApiPrefix = "/api/v1/study-groups/{studyGroupId}/members";

	@Test
	@DisplayName("스터디 그룹에 가입신청을한다.")
	void postApplyStudyGroup() throws Exception {
		//given
		StudyGroupMemberIdResponse idResponse = new StudyGroupMemberIdResponse(1L);
		given(studyGroupMemberService.applyForJoinStudyGroup(any(), any())).willReturn(idResponse);

		//when
		ResultActions resultActions = mockMvc.perform(
			post(studyGroupMemberApiPrefix, studyGroupId).contentType(APPLICATION_JSON));

		resultActions
			.andExpectAll(
				status().isOk(),
				content().json(objectMapper.writeValueAsString(idResponse)))
			.andDo(
				document(COMMON_DOCS_NAME,
					requestHeaders(contentType(), host()),
					pathParameters(studyGroupIdPath()),
					responseHeaders(contentType()),
					responseFields(studyGroupMemberIdField())));
	}

	@Test
	@DisplayName("스터디 가입신청을 수락한다.")
	void acceptRequestForJoin() throws Exception {
		//given
		StudyGroupMemberIdResponse idResponse = toStudyGroupMemberIdResponse(studyGroupMemberId);
		given(studyGroupMemberService.acceptRequestForJoin(any(), any(), any())).willReturn(idResponse);

		//when
		ResultActions resultActions = mockMvc.perform(
			patch(studyGroupMemberApiPrefix + "/{studyGroupMemberId}", studyGroupId, studyGroupMemberId)
				.contentType(APPLICATION_JSON));

		//then
		resultActions
			.andExpectAll(
				status().isOk(),
				content().json(objectMapper.writeValueAsString(idResponse)),
				content().contentType(APPLICATION_JSON))
			.andDo(document(COMMON_DOCS_NAME,
				requestHeaders(contentType(), host()),
				pathParameters(studyGroupIdPath(), studyGroupMemberIdPath()),
				responseHeaders(contentType(), contentLength()),
				responseFields(studyGroupMemberIdField())));
	}

	@Test
	@DisplayName("스터디 그룹 멤버를 삭제한다.")
	void deleteStudyGroupMember() throws Exception {
		//given
		willDoNothing().given(studyGroupMemberService).deleteStudyGroupMember(any(), any(), any());

		//when
		ResultActions resultActions = mockMvc.perform(
			delete(studyGroupMemberApiPrefix + "/{studyGroupMemberId}", studyGroupId, studyGroupMemberId)
				.contentType(APPLICATION_JSON_VALUE));

		//then
		resultActions
			.andExpectAll(
				status().isOk(),
				content().contentType(APPLICATION_JSON))
			.andDo(document(COMMON_DOCS_NAME,
				requestHeaders(contentType(), host()),
				pathParameters(studyGroupIdPath(), studyGroupMemberIdPath()),
				responseHeaders(contentType())));
	}

	private StudyGroupMemberIdResponse toStudyGroupMemberIdResponse(Long studyGroupMemberId) {
		return new StudyGroupMemberIdResponse(studyGroupMemberId);
	}

	private ParameterDescriptor studyGroupMemberIdPath() {
		return parameterWithName(STUDY_GROUP_MEMBER_ID.value()).description("스터디 그룹 멤버 아이디");
	}

	private FieldDescriptor studyGroupMemberIdField() {
		return fieldWithPath(STUDY_GROUP_MEMBER_ID.value()).type(NUMBER).description("스터디 그룹  멤버 아이디");
	}
}
