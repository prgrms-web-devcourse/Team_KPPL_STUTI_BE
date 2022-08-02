package prgrms.project.stuti.domain.studygroup.controller;

import static org.mockito.BDDMockito.*;
import static org.springframework.http.HttpHeaders.*;
import static org.springframework.http.MediaType.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static prgrms.project.stuti.domain.studygroup.controller.CommonStudyDomainDescriptor.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.headers.HeaderDescriptor;
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

	@Test
	@DisplayName("스터디 가입신청을 수락한다.")
	void acceptRequestForJoin() throws Exception {
		//given
		StudyMemberIdResponse idResponse = toStudyMemberIdResponse(1L);
		given(studyMemberService.acceptRequestForJoin(any(), any(), any())).willReturn(idResponse);

		//when
		ResultActions resultActions = mockMvc.perform(
			patch("/api/v1/study-groups/{studyGroupId}/study-members/{studyMemberId}",
				1L, idResponse.studyMemberId())
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
				responseFields(studyMemberIdField())
			));
	}

	private StudyMemberIdResponse toStudyMemberIdResponse(Long studyMemberId) {
		return new StudyMemberIdResponse(studyMemberId);
	}

	private ParameterDescriptor studyMemberIdPath() {
		return parameterWithName("studyMemberId").description("스터디 멤버 아이디");
	}

	private HeaderDescriptor contentLength() {
		return headerWithName(CONTENT_LENGTH).description("컨텐츠 길이");
	}

	private FieldDescriptor studyMemberIdField() {
		return fieldWithPath("studyMemberId").type(NUMBER).description("스터디 멤버 아이디");
	}
}
