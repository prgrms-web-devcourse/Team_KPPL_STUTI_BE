package prgrms.project.stuti.domain.studygroup.controller;

import static org.mockito.ArgumentMatchers.*;
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

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
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
import prgrms.project.stuti.domain.member.model.Mbti;
import prgrms.project.stuti.domain.studygroup.model.Region;
import prgrms.project.stuti.domain.studygroup.model.Topic;
import prgrms.project.stuti.domain.studygroup.service.StudyGroupService;
import prgrms.project.stuti.domain.studygroup.service.response.StudyGroupDetailResponse;
import prgrms.project.stuti.domain.studygroup.service.response.StudyGroupIdResponse;
import prgrms.project.stuti.domain.studygroup.service.response.StudyGroupLeaderResponse;
import prgrms.project.stuti.domain.studygroup.service.response.StudyGroupResponse;
import prgrms.project.stuti.global.page.CursorPageResponse;

@WebMvcTest(controllers = StudyGroupRestController.class)
class StudyGroupRestControllerTest extends TestConfig {

	@MockBean
	private StudyGroupService studyGroupService;

	private final String studyGroupApiPrefix = "/api/v1/study-groups";

	private final Long studyGroupId = 1L;

	private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	@Test
	@DisplayName("새로운 스터디 그룹을 생성한다.")
	void postStudyGroup() throws Exception {
		//given
		StudyGroupIdResponse idResponse = new StudyGroupIdResponse(studyGroupId);
		MultiValueMap<String, String> createParams = toCreateParams();
		given(studyGroupService.createStudyGroup(any())).willReturn(idResponse);

		//when
		ResultActions resultActions = mockMvc.perform(multipart(studyGroupApiPrefix)
			.file("imageFile", getMultipartFileBytes())
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
					requestParts(imageFile()),
					requestParameters(title()).and(parametersOfCreateStudyGroup()).and(description()),
					responseHeaders(contentType()).and(location()),
					responseFields(studyGroupIdField())));
	}

	@Test
	@DisplayName("전체 스터디 그룹을 동적으로 페이징 조회한다.")
	void getStudyGroups() throws Exception {
		//given
		CursorPageResponse<StudyGroupResponse> pageResponse = toStudyGroupPageResponse();
		given(studyGroupService.getStudyGroups(any())).willReturn(pageResponse);

		//when
		ResultActions resultActions = mockMvc.perform(
			get(studyGroupApiPrefix)
				.queryParams(toGetStudyGroupsParams())
			.contentType(APPLICATION_JSON));

		//then
		resultActions
			.andExpectAll(
				status().isOk(),
				content().json(objectMapper.writeValueAsString(pageResponse)))
			.andDo(
				document(COMMON_DOCS_NAME,
					requestHeaders(contentType(), host()),
					requestParameters(conditionParams()),
					responseHeaders(contentType(), contentLength()),
					responseFields(pageResponseFields())
				));
	}

	@Test
	@DisplayName("스터디 그룹을 상세조회한다.")
	void getStudyGroup() throws Exception {
		//given
		StudyGroupDetailResponse detailResponse = toDetailResponse();
		given(studyGroupService.getStudyGroup(any())).willReturn(detailResponse);

		//when
		ResultActions resultActions = mockMvc.perform(
			get(studyGroupApiPrefix + "/{studyGroupId}", studyGroupId)
				.contentType(APPLICATION_JSON));

		//then
		resultActions
			.andExpectAll(
				status().isOk(),
				content().json(objectMapper.writeValueAsString(detailResponse)))
			.andDo(
				document(COMMON_DOCS_NAME,
					requestHeaders(contentType(), host()),
					pathParameters(studyGroupIdPath()),
					responseHeaders(contentType()),
					responseFields(studyGroupDetailResponseFields())
						.andWithPrefix("leader.", leaderResponseFields())
				));
	}

	@Test
	@DisplayName("스터디 그룹을 업데이트한다.")
	void patchStudyGroup() throws Exception {
		//given
		StudyGroupIdResponse idResponse = new StudyGroupIdResponse(studyGroupId);
		MultiValueMap<String, String> updateParams = toUpdateParams();
		given(studyGroupService.updateStudyGroup(any())).willReturn(idResponse);

		//when
		ResultActions resultActions = mockMvc.perform(
			multipart(studyGroupApiPrefix + "/{studyGroupId}", idResponse.studyGroupId())
				.file("imageFile", getMultipartFileBytes())
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
					requestParts(imageFile()),
					requestParameters(title(), description()),
					responseHeaders(contentType()),
					responseFields(studyGroupIdField())));
	}

	@Test
	@DisplayName("스터디 그룹을 삭제한다.")
	void deleteStudyGroup() throws Exception {
		//given
		doNothing().when(studyGroupService).deleteStudyGroup(any(), any());

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

	private MultiValueMap<String, String> toCreateParams() {
		MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
		map.add(TITLE.value(), "test title");
		map.add(TOPIC.value(), String.valueOf(Topic.AI));
		map.add(IS_ONLINE.value(), "false");
		map.add(REGION.value(), String.valueOf(Region.SEOUL));
		map.add(PREFERRED_MBTIS.value(), "INFJ, ENFP");
		map.add(NUMBER_OF_RECRUITS.value(), "5");
		map.add(START_DATE_TIME.value(), LocalDateTime.now().plusDays(10).format(dateTimeFormatter));
		map.add(END_DATE_TIME.value(), LocalDateTime.now().plusMonths(10).format(dateTimeFormatter));
		map.add(DESCRIPTION.value(), "test description");

		return map;
	}

	private MultiValueMap<String, String> toGetStudyGroupsParams() {
		MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
		map.add(MBTI.value(), null);
		map.add(TOPIC.value(), null);
		map.add(REGION.value(), null);
		map.add("studyGroupMemberRole", "STUDY_LEADER");
		map.add("lastStudyGroupId", null);
		map.add("size", "10");

		return map;
	}

	private StudyGroupDetailResponse toDetailResponse() {

		return StudyGroupDetailResponse
			.builder()
			.studyGroupId(studyGroupId)
			.topic(Topic.DEV_OPS.getValue())
			.title("test title")
			.imageUrl("test image url")
			.leader(
				StudyGroupLeaderResponse.builder()
					.memberId(1L)
					.profileImageUrl("test profile image url")
					.nickname("nickname")
					.field("BACKEND")
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

	private MultiValueMap<String, String> toUpdateParams() {
		MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
		map.add(TITLE.value(), "update title");
		map.add(DESCRIPTION.value(), "update description");

		return map;
	}

	private byte[] getMultipartFileBytes() throws IOException {
		return new MockMultipartFile("testImageFile", "testImageFile.png",
			"image/png", "test".getBytes()).getBytes();
	}

	private CursorPageResponse<StudyGroupResponse> toStudyGroupPageResponse() {
		final List<StudyGroupResponse> studyGroupResponses = List.of(StudyGroupResponse
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

	private HeaderDescriptor location() {
		return headerWithName(HttpHeaders.LOCATION).description("생성된 리소스 주소");
	}

	private ParameterDescriptor title() {
		return parameterWithName(TITLE.value()).description("스터디 그룹 제목");
	}

	private ParameterDescriptor description() {
		return parameterWithName(DESCRIPTION.value()).description("스터디 그룹 설명");
	}

	private List<ParameterDescriptor> parametersOfCreateStudyGroup() {
		return List.of(parameterWithName(TOPIC.value()).description("스터디 주제"),
			parameterWithName(IS_ONLINE.value()).description("온라인 / 오프라인 여부"),
			parameterWithName(REGION.value()).description("지역"),
			parameterWithName(PREFERRED_MBTIS.value()).description("선호하는 MBTI 목록"),
			parameterWithName(NUMBER_OF_RECRUITS.value()).description("모집 인원수"),
			parameterWithName(START_DATE_TIME.value()).description("스터디 시작일자"),
			parameterWithName(END_DATE_TIME.value()).description("스터디 종료일자"));
	}

	private RequestPartDescriptor imageFile() {
		return partWithName(IMAGE_FILE.value()).description("이미지 파일");
	}

	private FieldDescriptor studyGroupIdField() {
		return fieldWithPath(STUDY_GROUP_ID.value()).type(NUMBER).description("스터디 그룹 아이디");
	}

	private List<FieldDescriptor> studyGroupDetailResponseFields() {
		return List.of(
			studyGroupIdField(),
			fieldWithPath(TITLE.value()).type(STRING).description("스터디 그룹 제목"),
			fieldWithPath(TOPIC.value()).type(STRING).description("스터디 주제"),
			fieldWithPath(IMAGE_URL.value()).type(STRING).description("스터디 이미지 url"),
			fieldWithPath(PREFERRED_MBTIS.value()).type(ARRAY).description("선호하는 MBTI 목록"),
			fieldWithPath(IS_ONLINE.value()).type(BOOLEAN).description("온라인 / 오프라인 여부"),
			fieldWithPath(REGION.value()).type(STRING).description("지역"),
			fieldWithPath(START_DATE_TIME.value()).type(STRING).description("스터디 시작일자"),
			fieldWithPath(END_DATE_TIME.value()).type(STRING).description("스터디 종료일자"),
			fieldWithPath(NUMBER_OF_MEMBERS.value()).type(NUMBER).description("스터디 멤버 인원수"),
			fieldWithPath(NUMBER_OF_RECRUITS.value()).type(NUMBER).description("모집 인원수"),
			fieldWithPath(DESCRIPTION.value()).type(STRING).description("스터디 그룹 설명")
		);
	}

	private List<FieldDescriptor> leaderResponseFields() {
		return List.of(
			fieldWithPath(MEMBER_ID.value()).type(NUMBER).description("회원 아이디"),
			fieldWithPath(PROFILE_IMAGE_URL.value()).type(STRING).description("프로필 이미지 url"),
			fieldWithPath(NICKNAME.value()).type(STRING).description("닉네임"),
			fieldWithPath(FIELD.value()).type(STRING).description("업무분야"),
			fieldWithPath(CAREER.value()).type(STRING).description("개발경력"),
			fieldWithPath(MBTI.value()).type(STRING).description("MBTI")
		);
	}

	private List<FieldDescriptor> pageResponseFields() {
		return List.of(
			subsectionWithPath("contents").type(ARRAY).description("페이징 컨텐츠"),
			fieldWithPath("hasNext").type(BOOLEAN).description("다음 컨텐츠 유무")
		);
	}

	private List<ParameterDescriptor> conditionParams() {
		return List.of(
			parameterWithName(MBTI.value()).description("검색 mbti"),
			parameterWithName(TOPIC.value()).description("검색 스터디 주제"),
			parameterWithName(REGION.value()).description("검색 스터디 지역"),
			parameterWithName("studyGroupMemberRole").description("검색 스터디 그룹 멤버 역할"),
			parameterWithName("lastStudyGroupId").description("마지막으로 본 스터디 그룹 아이디"),
			parameterWithName("size").description("스터디 그룹 리스트 사이즈")
		);
	}
}

