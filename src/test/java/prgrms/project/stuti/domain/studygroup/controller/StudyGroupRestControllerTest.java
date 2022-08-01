package prgrms.project.stuti.domain.studygroup.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static prgrms.project.stuti.domain.studygroup.controller.StudyGroupRestControllerTest.Field.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
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
import prgrms.project.stuti.domain.studygroup.model.Region;
import prgrms.project.stuti.domain.studygroup.model.Topic;
import prgrms.project.stuti.domain.studygroup.service.studygroup.StudyGroupService;
import prgrms.project.stuti.domain.studygroup.service.dto.StudyGroupIdResponse;

@WebMvcTest(controllers = StudyGroupRestController.class)
class StudyGroupRestControllerTest extends TestConfig {

	@MockBean
	private StudyGroupService studyGroupService;
	private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	@Test
	@DisplayName("새로운 스터디 그룹을 생성한다.")
	void postStudyGroup() throws Exception {
		//given
		StudyGroupIdResponse idResponse = new StudyGroupIdResponse(1L);
		MultiValueMap<String, String> createParams = toCreateParams();

		given(studyGroupService.createStudyGroup(any())).willReturn(idResponse);

		//when
		ResultActions resultActions = mockMvc.perform(multipart("/api/v1/study-groups")
			.file("imageFile", getMultipartFileBytes())
			.params(createParams)
			.contentType(MediaType.MULTIPART_FORM_DATA));

		//then
		resultActions
			.andExpectAll(
				status().isCreated(),
				content().json(objectMapper.writeValueAsString(idResponse)))
			.andDo(
				document(COMMON_DOCS_NAME,
					requestHeaders(contentType()).and(host()),
					requestParts(imageFile()),
					requestParameters(title()).and(parametersOfCreateStudyGroup()).and(description()),
					responseHeaders(contentType()).and(location()),
					responseFields(studyGroupId())));
	}

	@Test
	@DisplayName("스터디 그룹을 업데이트한다.")
	void patchStudyGroup() throws Exception {
		//given
		StudyGroupIdResponse idResponse = new StudyGroupIdResponse(1L);
		MultiValueMap<String, String> updateParams = toUpdateParams();

		given(studyGroupService.updateStudyGroup(any())).willReturn(idResponse);

		//when
		ResultActions resultActions = mockMvc.perform(
			multipart(HttpMethod.PATCH, "/api/v1/study-groups/{studyGroupId}", idResponse.studyGroupId())
				.file("imageFile", getMultipartFileBytes())
				.params(updateParams)
				.contentType(MediaType.MULTIPART_FORM_DATA));

		//then
		resultActions
			.andExpectAll(
				status().isOk(),
				content().json(objectMapper.writeValueAsString(idResponse)))
			.andDo(
				document(COMMON_DOCS_NAME,
					requestHeaders(contentType()).and(host()),
					requestParts(imageFile()),
					requestParameters(title(), description()),
					responseHeaders(contentType()),
					responseFields(studyGroupId())));
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

	private MultiValueMap<String, String> toUpdateParams() {
		MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
		map.add(TITLE.value(), "update title");
		map.add(DESCRIPTION.value(), "update description");

		return map;
	}

	private byte[] getMultipartFileBytes() throws IOException {
		return new MockMultipartFile("testImageFile", "testImageFile.png",
			"image/png", "abcde".getBytes()).getBytes();
	}

	private List<HeaderDescriptor> contentType() {
		return List.of(
			headerWithName(HttpHeaders.CONTENT_TYPE).description("컨텐츠 타입")
		);
	}

	private HeaderDescriptor location() {
		return headerWithName(HttpHeaders.LOCATION).description("생성된 리소스 주소");
	}

	private HeaderDescriptor host() {
		return headerWithName(HttpHeaders.HOST).description("호스트");
	}

	private FieldDescriptor studyGroupId() {
		return fieldWithPath(STUDY_GROUP_ID.value()).type(NUMBER).description("스터디 그룹 아이디");
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
			parameterWithName(PREFERRED_MBTIS.value()).description("선호하는 MBTI"),
			parameterWithName(NUMBER_OF_RECRUITS.value()).description("모집 인원"),
			parameterWithName(START_DATE_TIME.value()).description("스터디 시작 일자"),
			parameterWithName(END_DATE_TIME.value()).description("스터디 종료 일자"));
	}

	private RequestPartDescriptor imageFile() {
		return partWithName(IMAGE_FILE.value()).description("이미지 파일");
	}

	enum Field {
		TITLE("title"), TOPIC("topic"), IS_ONLINE("isOnline"),
		REGION("region"), PREFERRED_MBTIS("preferredMBTIs"),
		NUMBER_OF_RECRUITS("numberOfRecruits"), START_DATE_TIME("startDateTime"),
		END_DATE_TIME("endDateTime"), DESCRIPTION("description"), IMAGE_FILE("imageFile"),
		STUDY_GROUP_ID("studyGroupId");

		private final String value;

		Field(String value) {
			this.value = value;
		}

		public String value() {
			return this.value;
		}
	}
}

