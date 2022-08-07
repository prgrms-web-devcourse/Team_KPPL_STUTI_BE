package prgrms.project.stuti.domain.studygroup.controller;

import static org.mockito.ArgumentMatchers.*;
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
import static prgrms.project.stuti.domain.studygroup.controller.StudyGroupTestUtils.CommonField.*;
import static prgrms.project.stuti.domain.studygroup.controller.StudyGroupTestUtils.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.headers.HeaderDescriptor;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.request.ParameterDescriptor;
import org.springframework.restdocs.request.RequestPartDescriptor;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import prgrms.project.stuti.config.TestConfig;
import prgrms.project.stuti.domain.member.model.Career;
import prgrms.project.stuti.domain.member.model.Field;
import prgrms.project.stuti.domain.member.model.Mbti;
import prgrms.project.stuti.domain.studygroup.model.Region;
import prgrms.project.stuti.domain.studygroup.model.Topic;
import prgrms.project.stuti.domain.studygroup.service.StudyGroupService;
import prgrms.project.stuti.domain.studygroup.service.response.StudyGroupIdResponse;
import prgrms.project.stuti.domain.studygroup.service.response.StudyGroupResponse;
import prgrms.project.stuti.domain.studygroup.service.response.StudyGroupResponse.StudyGroupLeader;
import prgrms.project.stuti.domain.studygroup.service.response.StudyGroupsResponse;
import prgrms.project.stuti.global.page.CursorPageResponse;

@WebMvcTest(controllers = StudyGroupRestController.class)
class StudyGroupRestControllerTest extends TestConfig {

	@MockBean
	private StudyGroupService studyGroupService;

	private final String studyGroupApiPrefix = "/api/v1/study-groups";

	private final Long studyGroupId = 1L;

	private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	@Test
	@DisplayName("스터디 그룹을 생성한다.")
	void createStudyGroup() throws Exception {
		//given
		StudyGroupIdResponse idResponse = toStudyGroupIdResponse();
		MultiValueMap<String, String> createParams = toStudyGroupCreateParams();
		given(studyGroupService.createStudyGroup(any())).willReturn(idResponse);

		//when
		ResultActions resultActions = mockMvc.perform(multipart(studyGroupApiPrefix)
			.file(IMAGE_FILE.field(), getMockImageFileBytes())
			.params(createParams)
			.contentType(MediaType.MULTIPART_FORM_DATA)
			.with(requestPostProcessor -> {
				requestPostProcessor.setMethod("POST");
				return requestPostProcessor;
			}));

		//then
		resultActions
			.andExpectAll(
				status().isCreated(),
				content().json(objectMapper.writeValueAsString(idResponse)))
			.andDo(
				document(COMMON_DOCS_NAME,
					requestHeaders(contentType(), host()),
					requestParts(imageFilePart()),
					requestParameters(studyGroupCreateParams()),
					responseHeaders(contentType(), location()),
					responseFields(studyGroupIdField())));
	}

	@Test
	@DisplayName("전체 스터디 그룹을 페이징 조회한다.")
	void getStudyGroups() throws Exception {
		//given
		CursorPageResponse<StudyGroupsResponse> pageResponse = toStudyGroupPageResponse();
		given(studyGroupService.getStudyGroups(any())).willReturn(pageResponse);

		//when
		ResultActions resultActions = mockMvc.perform(
			get(studyGroupApiPrefix)
				.queryParams(toStudyGroupFindConditionParams())
				.contentType(APPLICATION_JSON));

		//then
		resultActions
			.andExpectAll(
				status().isOk(),
				content().json(objectMapper.writeValueAsString(pageResponse)))
			.andDo(
				document(COMMON_DOCS_NAME,
					requestHeaders(contentType(), host()),
					requestParameters(findConditionParams()),
					responseHeaders(contentType(), contentLength()),
					responseFields(pageFields())));
	}

	@Test
	@DisplayName("나의 스터디 그룹을 페이징 조회한다.")
	void getMyStudyGroups() throws Exception {
		//given
		CursorPageResponse<StudyGroupsResponse> pageResponse = toStudyGroupPageResponse();
		given(studyGroupService.getStudyGroups(any())).willReturn(pageResponse);

		//when
		ResultActions resultActions = mockMvc.perform(
			get(studyGroupApiPrefix + "/my-page")
				.queryParams(toStudyGroupFindConditionParams())
				.contentType(APPLICATION_JSON));

		//then
		resultActions
			.andExpectAll(
				status().isOk(),
				content().json(objectMapper.writeValueAsString(pageResponse)))
			.andDo(
				document(COMMON_DOCS_NAME,
					requestHeaders(contentType(), host()),
					requestParameters(findConditionParams()),
					responseHeaders(contentType(), contentLength()),
					responseFields(pageFields())));
	}

	@Test
	@DisplayName("스터디 그룹을 상세조회한다.")
	void getStudyGroup() throws Exception {
		//given
		StudyGroupResponse studyGroupResponse = toStudyGroupResponse();
		given(studyGroupService.getStudyGroup(any())).willReturn(studyGroupResponse);

		//when
		ResultActions resultActions = mockMvc.perform(
			get(studyGroupApiPrefix + "/{studyGroupId}", studyGroupId)
				.contentType(APPLICATION_JSON));

		//then
		resultActions
			.andExpectAll(
				status().isOk(),
				content().json(objectMapper.writeValueAsString(studyGroupResponse)))
			.andDo(
				document(COMMON_DOCS_NAME,
					requestHeaders(contentType(), host()),
					pathParameters(studyGroupIdPath()),
					responseHeaders(contentType(), contentLength()),
					responseFields(studyGroupFields())
						.andWithPrefix("leader.", leaderFields())));
	}

	@Test
	@DisplayName("스터디 그룹을 업데이트한다.")
	void updateStudyGroup() throws Exception {
		//given
		StudyGroupIdResponse idResponse = toStudyGroupIdResponse();
		MultiValueMap<String, String> updateParams = toStudyGroupUpdateParams();
		given(studyGroupService.updateStudyGroup(any())).willReturn(idResponse);

		//when
		ResultActions resultActions = mockMvc.perform(
			multipart(studyGroupApiPrefix + "/{studyGroupId}", idResponse.studyGroupId())
				.file("imageFile", getMockImageFileBytes())
				.params(updateParams)
				.contentType(MediaType.MULTIPART_FORM_DATA)
				.with(requestPostProcessor -> {
					requestPostProcessor.setMethod("PATCH");
					return requestPostProcessor;
				}));

		//then
		resultActions
			.andExpectAll(
				status().isOk(),
				content().json(objectMapper.writeValueAsString(idResponse)))
			.andDo(
				document(COMMON_DOCS_NAME,
					requestHeaders(contentType(), host()),
					requestParts(imageFilePart()),
					requestParameters(titleParam(), descriptionParam()),
					responseHeaders(contentType(), contentLength()),
					responseFields(studyGroupIdField())));
	}

	@Test
	@DisplayName("스터디 그룹을 삭제한다.")
	void deleteStudyGroup() throws Exception {
		//given
		doNothing().when(studyGroupService).deleteStudyGroup(any());

		//when
		ResultActions resultActions = mockMvc.perform(
			delete(studyGroupApiPrefix + "/{studyGroupId}", studyGroupId)
				.contentType(APPLICATION_JSON));

		// then
		resultActions
			.andExpectAll(
				status().isOk(),
				content().contentType(APPLICATION_JSON))
			.andDo(
				document(COMMON_DOCS_NAME,
					requestHeaders(contentType(), host()),
					pathParameters(studyGroupIdPath()),
					responseHeaders(contentType())));
	}

	private StudyGroupIdResponse toStudyGroupIdResponse() {
		return new StudyGroupIdResponse(studyGroupId);
	}

	private CursorPageResponse<StudyGroupsResponse> toStudyGroupPageResponse() {
		List<StudyGroupsResponse> studyGroupResponses =
			List.of(
				StudyGroupsResponse
					.builder()
					.studyGroupId(studyGroupId)
					.memberId(1L)
					.thumbnailUrl("test thumbnail url")
					.topic(Topic.DEV_OPS.getValue())
					.title("test title")
					.preferredMBTIs(Set.of(Mbti.ENFJ, Mbti.ESFJ, Mbti.ENTJ))
					.region(Region.SEOUL.getValue())
					.startDateTime(LocalDateTime.now().plusDays(10))
					.endDateTime(LocalDateTime.now().plusMonths(10))
					.numberOfMembers(3)
					.numberOfRecruits(5)
					.build(),

				StudyGroupsResponse
					.builder()
					.studyGroupId(studyGroupId)
					.memberId(1L)
					.thumbnailUrl("test thumbnail url")
					.topic(Topic.DEV_OPS.getValue())
					.title("test title")
					.preferredMBTIs(Set.of(Mbti.ENFJ, Mbti.ESFJ, Mbti.ENTJ))
					.region(Region.SEOUL.getValue())
					.startDateTime(LocalDateTime.now().plusDays(10))
					.endDateTime(LocalDateTime.now().plusMonths(10))
					.numberOfMembers(3)
					.numberOfRecruits(5)
					.build());

		return new CursorPageResponse<>(studyGroupResponses, true);
	}

	private StudyGroupResponse toStudyGroupResponse() {

		return StudyGroupResponse
			.builder()
			.studyGroupId(studyGroupId)
			.topic(Topic.DEV_OPS.getValue())
			.title("test title")
			.imageUrl("test image url")
			.leader(
				StudyGroupLeader.builder()
					.memberId(1L)
					.profileImageUrl("test profile image url")
					.nickname("nickname")
					.field(Field.BACKEND.getFieldValue())
					.career(Career.JUNIOR.getCareerValue())
					.mbti(Mbti.ENFJ)
					.build())
			.preferredMBTIs(Set.of(Mbti.ENFJ, Mbti.INFJ))
			.isOnline(false)
			.region(Region.DAEJEON.getValue())
			.startDateTime(LocalDateTime.now().plusDays(10))
			.endDateTime(LocalDateTime.now().plusMonths(10))
			.numberOfMembers(5)
			.numberOfRecruits(5)
			.description("test description")
			.build();
	}

	private byte[] getMockImageFileBytes() throws IOException {
		return new MockMultipartFile(
			"testImageFile",
			"testImageFile.png",
			"image/png",
			"test".getBytes())
			.getBytes();
	}

	private MultiValueMap<String, String> toStudyGroupCreateParams() {
		MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
		map.add(TITLE.field(), "test title");
		map.add(TOPIC.field(), String.valueOf(Topic.AI));
		map.add(IS_ONLINE.field(), "false");
		map.add(REGION.field(), String.valueOf(Region.SEOUL));
		map.add(PREFERRED_MBTIS.field(), "INFJ, ENFP");
		map.add(NUMBER_OF_RECRUITS.field(), "5");
		map.add(START_DATE_TIME.field(), LocalDateTime.now().plusDays(10).format(dateTimeFormatter));
		map.add(END_DATE_TIME.field(), LocalDateTime.now().plusMonths(10).format(dateTimeFormatter));
		map.add(DESCRIPTION.field(), "test description");

		return map;
	}

	private MultiValueMap<String, String> toStudyGroupFindConditionParams() {
		MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
		map.add(MBTI.field(), null);
		map.add(TOPIC.field(), null);
		map.add(REGION.field(), null);
		map.add(STUDY_GROUP_MEMBER_ROLE.field(), "STUDY_LEADER");
		map.add(LAST_STUDY_GROUP_ID.field(), null);
		map.add(SIZE.field(), "10");

		return map;
	}

	private MultiValueMap<String, String> toStudyGroupUpdateParams() {
		MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
		map.add(TITLE.field(), "update title");
		map.add(DESCRIPTION.field(), "update description");

		return map;
	}

	private HeaderDescriptor location() {
		return headerWithName(LOCATION).description("생성된 리소스 주소");
	}

	private RequestPartDescriptor imageFilePart() {
		return partWithName(IMAGE_FILE.field()).description(IMAGE_FILE.description());
	}

	private ParameterDescriptor titleParam() {
		return parameterWithName(TITLE.field()).description(TITLE.description());
	}

	private ParameterDescriptor descriptionParam() {
		return parameterWithName(DESCRIPTION.field()).description(DESCRIPTION.description());
	}

	private List<ParameterDescriptor> findConditionParams() {
		return List.of(
			parameterWithName(MBTI.field()).description(MBTI.description()),
			parameterWithName(TOPIC.field()).description(TOPIC.description()),
			parameterWithName(REGION.field()).description(REGION.description()),
			parameterWithName(STUDY_GROUP_MEMBER_ROLE.field()).description(STUDY_GROUP_MEMBER_ROLE.description()),
			parameterWithName(LAST_STUDY_GROUP_ID.field()).description(LAST_STUDY_GROUP_ID.description()),
			parameterWithName(SIZE.field()).description(SIZE.description())
		);
	}

	private List<ParameterDescriptor> studyGroupCreateParams() {
		return List.of(
			titleParam(),
			parameterWithName(TOPIC.field()).description(TOPIC.description()),
			parameterWithName(IS_ONLINE.field()).description(IS_ONLINE.description()),
			parameterWithName(REGION.field()).description(REGION.description()),
			parameterWithName(NUMBER_OF_RECRUITS.field()).description(NUMBER_OF_RECRUITS.description()),
			parameterWithName(START_DATE_TIME.field()).description(START_DATE_TIME.description()),
			parameterWithName(END_DATE_TIME.field()).description(END_DATE_TIME.description()),
			parameterWithName(PREFERRED_MBTIS.field()).description(PREFERRED_MBTIS.description()),
			descriptionParam());
	}

	private FieldDescriptor studyGroupIdField() {
		return fieldWithPath(STUDY_GROUP_ID.field()).type(NUMBER).description(STUDY_GROUP_ID.description());
	}

	private List<FieldDescriptor> studyGroupFields() {
		return List.of(
			studyGroupIdField(),
			fieldWithPath(TITLE.field()).type(STRING).description(TITLE.description()),
			fieldWithPath(TOPIC.field()).type(STRING).description(TOPIC.description()),
			fieldWithPath(IMAGE_URL.field()).type(STRING).description(IMAGE_URL.description()),
			fieldWithPath(PREFERRED_MBTIS.field()).type(ARRAY).description(PREFERRED_MBTIS.description()),
			fieldWithPath(IS_ONLINE.field()).type(BOOLEAN).description(IS_ONLINE.description()),
			fieldWithPath(REGION.field()).type(STRING).description(REGION.description()),
			fieldWithPath(START_DATE_TIME.field()).type(STRING).description(START_DATE_TIME.description()),
			fieldWithPath(END_DATE_TIME.field()).type(STRING).description(END_DATE_TIME.description()),
			fieldWithPath(NUMBER_OF_MEMBERS.field()).type(NUMBER).description(NUMBER_OF_MEMBERS.description()),
			fieldWithPath(NUMBER_OF_RECRUITS.field()).type(NUMBER).description(NUMBER_OF_RECRUITS.description()),
			fieldWithPath(DESCRIPTION.field()).type(STRING).description(DESCRIPTION.description())
		);
	}

	private List<FieldDescriptor> leaderFields() {
		return List.of(
			fieldWithPath(MEMBER_ID.field()).type(NUMBER).description(MEMBER_ID.description()),
			fieldWithPath(PROFILE_IMAGE_URL.field()).type(STRING).description(PROFILE_IMAGE_URL.description()),
			fieldWithPath(NICKNAME.field()).type(STRING).description(NICKNAME.description()),
			fieldWithPath(FIELD.field()).type(STRING).description(FIELD.description()),
			fieldWithPath(CAREER.field()).type(STRING).description(CAREER.description()),
			fieldWithPath(MBTI.field()).type(STRING).description(MBTI.description())
		);
	}

	private List<FieldDescriptor> pageFields() {
		return List.of(
			subsectionWithPath(PAGE_CONTENTS.field()).type(ARRAY).description(PAGE_CONTENTS.description()),
			fieldWithPath(HAS_NEXT.field()).type(BOOLEAN).description(HAS_NEXT.description())
		);
	}
}

