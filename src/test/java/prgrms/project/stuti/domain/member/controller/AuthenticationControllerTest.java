package prgrms.project.stuti.domain.member.controller;

import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Optional;

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
import prgrms.project.stuti.domain.member.controller.dto.MemberSaveRequest;
import prgrms.project.stuti.domain.member.controller.mapper.MemberMapper;
import prgrms.project.stuti.domain.member.model.MemberRole;
import prgrms.project.stuti.domain.member.service.MemberService;
import prgrms.project.stuti.domain.member.service.dto.MemberResponse;
import prgrms.project.stuti.global.cache.model.TemporaryMember;
import prgrms.project.stuti.global.cache.service.BlackListTokenService;
import prgrms.project.stuti.global.cache.service.RefreshTokenService;
import prgrms.project.stuti.global.cache.service.TemporaryMemberService;
import prgrms.project.stuti.global.token.TokenGenerator;
import prgrms.project.stuti.global.token.TokenService;
import prgrms.project.stuti.global.token.Tokens;

@WebMvcTest(controllers = AuthenticationController.class)
class AuthenticationControllerTest extends TestConfig {
	@Autowired
	protected MockMvc mockMvc;

	@Autowired
	protected ObjectMapper objectMapper;

	@MockBean
	MemberService memberService;
	@MockBean
	TokenService tokenService;
	@MockBean
	BlackListTokenService blackListTokenService;
	@MockBean
	RefreshTokenService refreshTokenService;
	@MockBean
	TemporaryMemberService temporaryMemberService;
	@MockBean
	TokenGenerator tokenGenerator;

	@Test
	@WithMockUser(roles = "USER")
	@DisplayName("/api/v1/signup 에서 회원가입")
	void postMember() throws Exception {
		// given
		MemberSaveRequest memberSaveRequest = MemberSaveRequest.builder()
			.email("test@test.com")
			.nickname("test")
			.field("BACKEND")
			.career("JUNIOR")
			.MBTI("ENFJ")
			.build();

		TemporaryMember temporaryMember = TemporaryMember.builder()
			.email("test@test.com")
			.imageUrl("test.s3.com")
			.nickname("test")
			.expiration(500000L)
			.build();

		MemberResponse memberResponse = MemberResponse.builder()
			.memberId(1L)
			.build();

		Tokens tokens = new Tokens("accessToken", "refreshToken");

		given(temporaryMemberService.findById(memberSaveRequest.email())).willReturn(Optional.of(temporaryMember));
		given(memberService.signup(MemberMapper.toMemberDto(memberSaveRequest), temporaryMember)).willReturn(
			memberResponse);
		given(tokenGenerator.generateTokens(memberSaveRequest.email(), MemberRole.ROLE_USER.stringValue)).willReturn(
			tokens);

		// when
		ResultActions resultActions = mockMvc.perform(
			post("/api/v1/signup")
				.with(SecurityMockMvcRequestPostProcessors.csrf())
				.content(objectMapper.writeValueAsString(memberSaveRequest))
				.contentType(MediaType.APPLICATION_JSON));

		// then
		resultActions
			.andExpect(status().isCreated())
			.andDo(print())
			.andDo(document(COMMON_DOCS_NAME,
				requestHeaders(
					headerWithName(HttpHeaders.CONTENT_TYPE).description("json 으로 전달")
				),
				requestFields(
					fieldWithPath("email").type(STRING).description("이메일"),
					fieldWithPath("nickname").type(STRING).description("닉네임"),
					fieldWithPath("field").type(STRING).description("개발 분야"),
					fieldWithPath("career").type(STRING).description("개발 경력"),
					fieldWithPath("MBTI").type(STRING).description("MBTI")
				),
				responseHeaders(
					headerWithName(HttpHeaders.LOCATION).description("main 으로 redirect"),
					headerWithName(HttpHeaders.CONTENT_TYPE).description("json 으로 전달")
				),
				responseFields(
					fieldWithPath("memberId").type(NUMBER).description("생성된 멤버 id")
				)));
	}

}