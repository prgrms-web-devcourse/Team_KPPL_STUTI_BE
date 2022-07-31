package prgrms.project.stuti.domain.member.controller;

import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;

import prgrms.project.stuti.config.TestConfig;
import prgrms.project.stuti.domain.member.controller.dto.MemberPutRequest;
import prgrms.project.stuti.domain.member.model.Career;
import prgrms.project.stuti.domain.member.model.Field;
import prgrms.project.stuti.domain.member.model.Mbti;
import prgrms.project.stuti.domain.member.service.MemberService;
import prgrms.project.stuti.domain.member.service.dto.MemberResponse;

@WebMvcTest(controllers = MemberController.class)
class MemberControllerTest extends TestConfig {

	@Autowired
	protected MockMvc mockMvc;

	@Autowired
	protected ObjectMapper objectMapper;

	@MockBean
	MemberService memberService;

	@Test
	@WithMockUser(roles = "MEMBER")
	@DisplayName("/api/v1/members/{memberId} 에서 멤버조회한다.")
	void getMember() throws Exception {
		// given
		Long memberId = 1L;

		MemberResponse memberResponse = MemberResponse.builder()
			.id(1L)
			.email("test@test.com")
			.profileImageUrl("s3.test.com")
			.nickname("test")
			.field(Field.ANDROID)
			.career(Career.JUNIOR)
			.MBTI(Mbti.ENFJ)
			.githubUrl("test.github")
			.blogUrl("test.blog")
			.build();

		given(memberService.getMember(memberId)).willReturn(memberResponse);

		// when
		ResultActions resultActions = mockMvc.perform(get("/api/v1/members/{memberId}", 1));

		// then
		resultActions
			.andExpectAll(status().isOk(),
				content().json(objectMapper.writeValueAsString(memberResponse)))
			.andDo(print())
			.andDo(document(COMMON_DOCS_NAME,
				responseHeaders(
					headerWithName(HttpHeaders.CONTENT_TYPE).description("json 으로 전달")
				),
				responseFields(
					fieldWithPath("id").type(NUMBER).description("멤버 id"),
					fieldWithPath("email").type(STRING).description("멤버 이메일"),
					fieldWithPath("profileImageUrl").type(STRING).description("멤버 프로필 url"),
					fieldWithPath("nickname").type(STRING).description("멤버 닉네임"),
					fieldWithPath("field").type(STRING).description("멤버 분야"),
					fieldWithPath("career").type(STRING).description("멤버 경력"),
					fieldWithPath("MBTI").type(STRING).description("멤버 MBTI"),
					fieldWithPath("githubUrl").type(STRING).description("멤버 깃허브"),
					fieldWithPath("blogUrl").type(STRING).description("멤버 블로그")
				)));
	}

	@Test
	@WithMockUser(roles = "MEMBER")
	@DisplayName("/api/v1/members/{memberId} 에서 멤버수정한다")
	void putMember() throws Exception {
		// given
		Long memberId = 1L;

		MemberPutRequest memberPutRequest = MemberPutRequest.builder()
			.id(1L)
			.email("edit@test.com")
			.profileImageUrl("s3.edit.com")
			.nickname("edit")
			.field(Field.ANDROID)
			.career(Career.JUNIOR)
			.MBTI(Mbti.ENFJ)
			.githubUrl("edit.github")
			.blogUrl("edit.blog")
			.build();

		MemberResponse memberResponse = MemberResponse.builder()
			.id(1L)
			.email("edit@test.com")
			.profileImageUrl("s3.edit.com")
			.nickname("edit")
			.field(Field.ANDROID)
			.career(Career.JUNIOR)
			.MBTI(Mbti.ENFJ)
			.githubUrl("edit.github")
			.blogUrl("edit.blog")
			.build();

		given(memberService.putMember(memberId, MemberMapper.toMemberPutDto(memberPutRequest))).willReturn(memberResponse);

		// when
		ResultActions resultActions = mockMvc.perform(put("/api/v1/members/{memberId}", 1)
			.with(SecurityMockMvcRequestPostProcessors.csrf())
			.content(objectMapper.writeValueAsString(memberPutRequest))
			.contentType(MediaType.APPLICATION_JSON));

		// then
		resultActions
			.andExpectAll(status().isCreated(),
				content().json(objectMapper.writeValueAsString(memberResponse)))
			.andDo(print())
			.andDo(document(COMMON_DOCS_NAME,
				requestHeaders(
					headerWithName(HttpHeaders.CONTENT_TYPE).description("json 으로 전달")
				),
				requestFields(
					fieldWithPath("id").type(NUMBER).description("멤버 id"),
					fieldWithPath("email").type(STRING).description("멤버 이메일"),
					fieldWithPath("profileImageUrl").type(STRING).description("멤버 프로필 url"),
					fieldWithPath("nickname").type(STRING).description("멤버 닉네임"),
					fieldWithPath("field").type(STRING).description("멤버 분야"),
					fieldWithPath("career").type(STRING).description("멤버 경력"),
					fieldWithPath("MBTI").type(STRING).description("멤버 MBTI"),
					fieldWithPath("githubUrl").type(STRING).description("멤버 깃허브"),
					fieldWithPath("blogUrl").type(STRING).description("멤버 블로그")
				),
				responseHeaders(
					headerWithName(HttpHeaders.CONTENT_TYPE).description("json 으로 전달")
				),
				responseFields(
					fieldWithPath("id").type(NUMBER).description("수정된 멤버 id"),
					fieldWithPath("email").type(STRING).description("수정된 멤버 이메일"),
					fieldWithPath("profileImageUrl").type(STRING).description("수정된 멤버 프로필 url"),
					fieldWithPath("nickname").type(STRING).description("수정된 멤버 닉네임"),
					fieldWithPath("field").type(STRING).description("수정된 멤버 분야"),
					fieldWithPath("career").type(STRING).description("수정된 멤버 경력"),
					fieldWithPath("MBTI").type(STRING).description("수정된 멤버 MBTI"),
					fieldWithPath("githubUrl").type(STRING).description("수정된 멤버 깃허브"),
					fieldWithPath("blogUrl").type(STRING).description("수정된 멤버 블로그")
				)));
	}
}