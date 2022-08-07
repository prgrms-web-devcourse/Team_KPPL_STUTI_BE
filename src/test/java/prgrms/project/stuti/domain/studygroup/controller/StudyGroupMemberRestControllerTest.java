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

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.request.ParameterDescriptor;
import org.springframework.test.web.servlet.ResultActions;

import prgrms.project.stuti.config.TestConfig;
import prgrms.project.stuti.domain.member.model.Career;
import prgrms.project.stuti.domain.member.model.Field;
import prgrms.project.stuti.domain.member.model.Mbti;
import prgrms.project.stuti.domain.studygroup.model.StudyGroupMemberRole;
import prgrms.project.stuti.domain.studygroup.model.Topic;
import prgrms.project.stuti.domain.studygroup.service.StudyGroupMemberService;
import prgrms.project.stuti.domain.studygroup.service.response.StudyGroupMemberIdResponse;
import prgrms.project.stuti.domain.studygroup.service.response.StudyGroupMembersResponse;
import prgrms.project.stuti.domain.studygroup.service.response.StudyGroupMembersResponse.StudyGroupMemberResponse;

@WebMvcTest(controllers = StudyGroupMemberRestController.class)
class StudyGroupMemberRestControllerTest extends TestConfig {

	@MockBean
	private StudyGroupMemberService studyGroupMemberService;

	private final Long studyGroupId = 1L;

	private final Long studyGroupMemberId = 1L;

	private final String studyGroupMemberApiPrefix = "/api/v1/study-groups/{studyGroupId}/members";

	@Test
	@DisplayName("스터디 그룹에 가입신청을 한다.")
	void applyForJoinStudyGroup() throws Exception {
		//given
		StudyGroupMemberIdResponse idResponse = toStudyGroupMemberIdResponse();
		given(studyGroupMemberService.applyForJoinStudyGroup(any())).willReturn(idResponse);

		//when
		ResultActions resultActions = mockMvc.perform(post(studyGroupMemberApiPrefix, studyGroupId)
			.contentType(APPLICATION_JSON));

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
	@DisplayName("스터디 그룹 멤버를 전체조회한다.")
	void getStudyGroupMembers() throws Exception {
		//given
		StudyGroupMembersResponse membersResponse = toStudyGroupMembersResponse();
		given(studyGroupMemberService.getStudyGroupMembers(any())).willReturn(membersResponse);

		//when
		ResultActions resultActions = mockMvc.perform(get(studyGroupMemberApiPrefix, studyGroupId)
			.contentType(APPLICATION_JSON));

		resultActions
			.andExpectAll(
				status().isOk(),
				content().json(objectMapper.writeValueAsString(membersResponse)))
			.andDo(
				document(COMMON_DOCS_NAME,
					requestHeaders(contentType(), host()),
					pathParameters(studyGroupIdPath()),
					responseHeaders(contentType()),
					responseFields(studyGroupMembersFields())
						.andWithPrefix(STUDY_MEMBERS.field(), studyGroupMemberFields())
						.andWithPrefix(STUDY_APPLICANTS.field(), studyGroupMemberFields())));
	}

	@Test
	@DisplayName("스터디 그룹 가입신청을 수락한다.")
	void acceptRequestForJoin() throws Exception {
		//given
		StudyGroupMemberIdResponse idResponse = toStudyGroupMemberIdResponse();
		given(studyGroupMemberService.acceptRequestForJoin(any())).willReturn(idResponse);

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
		willDoNothing().given(studyGroupMemberService).deleteStudyGroupMember(any());

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

	private StudyGroupMemberIdResponse toStudyGroupMemberIdResponse() {
		return new StudyGroupMemberIdResponse(studyGroupMemberId);
	}

	private StudyGroupMembersResponse toStudyGroupMembersResponse() {
		StudyGroupMemberResponse studyMember = StudyGroupMemberResponse
			.builder()
			.studyGroupMemberId(2L)
			.profileImageUrl("test profile image url")
			.nickname("test nickname")
			.field(Field.ANDROID.getFieldValue())
			.career(Career.JUNIOR.getCareerValue())
			.mbti(Mbti.ENFJ)
			.studyGroupMemberRole(StudyGroupMemberRole.STUDY_MEMBER.getValue())
			.build();

		StudyGroupMemberResponse studyApplicant = StudyGroupMemberResponse
			.builder()
			.studyGroupMemberId(3L)
			.profileImageUrl("test profile image url2")
			.nickname("test nickname2")
			.field(Field.IOS.getFieldValue())
			.career(Career.MASTER.getCareerValue())
			.mbti(Mbti.INFJ)
			.studyGroupMemberRole(StudyGroupMemberRole.STUDY_APPLICANT.getValue())
			.build();
		return StudyGroupMembersResponse
			.builder()
			.studyGroupId(studyGroupId)
			.topic(Topic.BACKEND.getValue())
			.title("test title")
			.numberOfMembers(2)
			.numberOfRecruits(5)
			.studyMembers(List.of(studyMember))
			.numberOfApplicants(1)
			.studyApplicants(List.of(studyApplicant))
			.build();
	}

	private ParameterDescriptor studyGroupMemberIdPath() {
		return parameterWithName(STUDY_GROUP_MEMBER_ID.field()).description(STUDY_GROUP_MEMBER_ID.description());
	}

	private FieldDescriptor studyGroupMemberIdField() {
		return fieldWithPath(STUDY_GROUP_MEMBER_ID.field()).type(NUMBER).description(STUDY_GROUP_MEMBER_ID.description());
	}

	private List<FieldDescriptor> studyGroupMembersFields() {
		return List.of(
			fieldWithPath(STUDY_GROUP_ID.field()).type(NUMBER).description(STUDY_GROUP_ID.description()),
			fieldWithPath(TOPIC.field()).type(STRING).description(TOPIC.description()),
			fieldWithPath(TITLE.field()).type(STRING).description(TITLE.description()),
			fieldWithPath(NUMBER_OF_MEMBERS.field()).type(NUMBER).description(NUMBER_OF_MEMBERS.description()),
			fieldWithPath(NUMBER_OF_RECRUITS.field()).type(NUMBER).description(NUMBER_OF_RECRUITS.description()),
			fieldWithPath(STUDY_MEMBERS.field()).type(ARRAY).description(STUDY_MEMBERS.description()),
			fieldWithPath("numberOfApplicants").type(NUMBER).description("스터디 그룹 지원자 수"),
			fieldWithPath(STUDY_APPLICANTS.field()).type(ARRAY).description(STUDY_APPLICANTS.description()));
	}

	private List<FieldDescriptor> studyGroupMemberFields() {
		return List.of(
			fieldWithPath(STUDY_GROUP_MEMBER_ID.field()).type(NUMBER).description(STUDY_GROUP_MEMBER_ID.description()),
			fieldWithPath(PROFILE_IMAGE_URL.field()).type(STRING).description(PROFILE_IMAGE_URL.description()),
			fieldWithPath(NICKNAME.field()).type(STRING).description(NICKNAME.description()),
			fieldWithPath(FIELD.field()).type(STRING).description(FIELD.description()),
			fieldWithPath(CAREER.field()).type(STRING).description(CAREER.description()),
			fieldWithPath(MBTI.field()).type(STRING).description(MBTI.description()),
			fieldWithPath(STUDY_GROUP_MEMBER_ROLE.field()).type(STRING)
				.description(STUDY_GROUP_MEMBER_ROLE.description()));
	}
}
