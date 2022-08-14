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
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.ResultActions;

import prgrms.project.stuti.config.TestConfig;
import prgrms.project.stuti.domain.member.controller.dto.MemberIdRequest;
import prgrms.project.stuti.domain.member.controller.dto.MemberSaveRequest;
import prgrms.project.stuti.domain.member.controller.dto.MemberSignupResponse;
import prgrms.project.stuti.domain.member.model.Career;
import prgrms.project.stuti.domain.member.model.Field;
import prgrms.project.stuti.domain.member.model.Mbti;
import prgrms.project.stuti.domain.member.model.MemberRole;
import prgrms.project.stuti.domain.member.service.AuthenticationService;
import prgrms.project.stuti.domain.member.service.dto.MemberResponse;
import prgrms.project.stuti.global.security.token.TokenService;
import prgrms.project.stuti.global.security.token.TokenType;
import prgrms.project.stuti.global.security.token.Tokens;

@WebMvcTest(controllers = AuthenticationController.class)
class AuthenticationControllerTest extends TestConfig {
	@MockBean
	TokenService tokenService;

	@MockBean
	AuthenticationService authenticationService;

	@Test
	@DisplayName("/api/v1/signup 에서 회원가입한다")
	void postMemberSignup() throws Exception {
		// given
		MemberSaveRequest memberSaveRequest = MemberSaveRequest.builder()
			.email("test@test.com")
			.nickname("test")
			.field(Field.ANDROID)
			.career(Career.JUNIOR)
			.MBTI(Mbti.ENFJ)
			.build();

		MemberResponse memberResponse = makeMemberResponse();
		String accessToken = "accessToken";
		MemberSignupResponse memberSignupResponse = new MemberSignupResponse(memberResponse, accessToken);

		Tokens tokens = new Tokens("accessToken", "RefreshToken");

		given(authenticationService.signupMember(MemberMapper.toMemberDto(memberSaveRequest))).willReturn(
			memberResponse);
		given(tokenService.generateTokens(memberResponse.id().toString(), MemberRole.ROLE_MEMBER.name()))
			.willReturn(tokens);

		// when
		ResultActions resultActions = mockMvc.perform(
			post("/api/v1/signup")
				.with(SecurityMockMvcRequestPostProcessors.csrf())
				.content(objectMapper.writeValueAsString(memberSaveRequest))
				.contentType(MediaType.APPLICATION_JSON));

		// then
		resultActions
			.andExpectAll(status().isCreated(),
				content().json(objectMapper.writeValueAsString(memberSignupResponse)))
			.andDo(print())
			.andDo(document(COMMON_DOCS_NAME,
				requestHeaders(
					headerWithName(HttpHeaders.CONTENT_TYPE).description("json 으로 전달")
				),
				requestFields(
					fieldWithPath("email").type(STRING).description("이메일"),
					fieldWithPath("nickname").type(STRING).description("닉네임"),
					fieldWithPath("field").type(STRING).description("분야"),
					fieldWithPath("career").type(STRING).description("경력"),
					fieldWithPath("MBTI").type(STRING).description("MBTI")
				),
				responseHeaders(
					headerWithName(HttpHeaders.LOCATION).description("main 으로 redirect"),
					headerWithName(HttpHeaders.CONTENT_TYPE).description("json 으로 전달")
				),
				responseFields(
					fieldWithPath("member").type(OBJECT).description("멤버"),
					fieldWithPath("member.id").type(NUMBER).description("아이디"),
					fieldWithPath("member.email").type(STRING).description("이메일"),
					fieldWithPath("member.profileImageUrl").type(STRING).description("프로필 이미지 url"),
					fieldWithPath("member.nickname").type(STRING).description("닉네임"),
					fieldWithPath("member.field").type(STRING).description("분야"),
					fieldWithPath("member.career").type(STRING).description("경력"),
					fieldWithPath("member.MBTI").type(STRING).description("MBTI"),
					fieldWithPath("member.githubUrl").type(STRING).description("깃허브 주소"),
					fieldWithPath("member.blogUrl").type(STRING).description("블로그 주소"),
					fieldWithPath("accesstoken").type(STRING).description("access 토큰값")
				)));
	}

	@Test
	@DisplayName("/api/v1/login 에서 로그인을 한다")
	void postLogin() throws Exception {
		// given
		MemberIdRequest memberIdRequest = new MemberIdRequest(1L);
		MemberResponse memberResponse = makeMemberResponse();
		Tokens tokens = new Tokens("accessToken", "RefreshToken");
		String accessToken = "accessToken";
		MemberSignupResponse memberSignupResponse = new MemberSignupResponse(memberResponse, accessToken);

		given(tokenService.generateTokens(memberIdRequest.id().toString(), MemberRole.ROLE_MEMBER.name())).willReturn(
			tokens);
		given(authenticationService.getMemberResponse(memberIdRequest.id())).willReturn(memberResponse);

		// when
		ResultActions resultActions = mockMvc.perform(
			post("/api/v1/login")
				.with(SecurityMockMvcRequestPostProcessors.csrf())
				.content(objectMapper.writeValueAsString(memberIdRequest))
				.contentType(MediaType.APPLICATION_JSON));

		// then
		resultActions
			.andExpectAll(status().isOk(),
				content().json(objectMapper.writeValueAsString(memberSignupResponse)))
			.andDo(print())
			.andDo(document(COMMON_DOCS_NAME,
				requestHeaders(
					headerWithName(HttpHeaders.CONTENT_TYPE).description("json 으로 전달")
				),
				requestFields(
					fieldWithPath("id").type(NUMBER).description("멤버 아이디")
				),
				responseHeaders(
					headerWithName(HttpHeaders.CONTENT_TYPE).description("json 으로 전달")
				),
				responseFields(
					fieldWithPath("member").type(OBJECT).description("멤버"),
					fieldWithPath("member.id").type(NUMBER).description("아이디"),
					fieldWithPath("member.email").type(STRING).description("이메일"),
					fieldWithPath("member.profileImageUrl").type(STRING).description("프로필 이미지 url"),
					fieldWithPath("member.nickname").type(STRING).description("닉네임"),
					fieldWithPath("member.field").type(STRING).description("분야"),
					fieldWithPath("member.career").type(STRING).description("경력"),
					fieldWithPath("member.MBTI").type(STRING).description("MBTI"),
					fieldWithPath("member.githubUrl").type(STRING).description("깃허브 주소"),
					fieldWithPath("member.blogUrl").type(STRING).description("블로그 주소"),
					fieldWithPath("accesstoken").type(STRING).description("access 토큰값")
				)));
	}

	@Test
	@DisplayName("/api/v1/logout 에서 로그아웃을 한다")
	void postLogout() throws Exception {
		// given
		String accessToken = "accessToken";
		String accessTokenWithType = "accessTokenWithType";

		given(tokenService.resolveToken(any())).willReturn(accessToken);
		given(tokenService.tokenWithType(accessToken, TokenType.JWT_BLACKLIST)).willReturn(accessTokenWithType);

		// when // then
		mockMvc.perform(
			post("/api/v1/logout")
				.with(SecurityMockMvcRequestPostProcessors.csrf())
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andDo(print());
	}

	@Test
	@DisplayName("/api/v1/auth 에서 프론트는 필요한 유저 정보를 가져온다.")
	void getMemberInfo() throws Exception {
		// given
		String accessToken = "accessToken";
		String refreshToken = "refreshToken";
		String memberId = "1";
		MemberResponse memberResponse = makeMemberResponse();

		given(tokenService.resolveToken(any())).willReturn(accessToken);
		given(authenticationService.checkAndGetRefreshToken(any())).willReturn(refreshToken);
		given(tokenService.getUid(any())).willReturn(memberId);
		given(authenticationService.getMemberResponse(Long.parseLong(memberId))).willReturn(memberResponse);

		// when
		ResultActions resultActions = mockMvc.perform(
			get("/api/v1/auth")
				.with(SecurityMockMvcRequestPostProcessors.csrf())
				.contentType(MediaType.APPLICATION_JSON));

		// then
		resultActions
			.andExpectAll(status().isOk(),
				content().json(objectMapper.writeValueAsString(memberResponse)))
			.andDo(print())
			.andDo(document(COMMON_DOCS_NAME,
				requestHeaders(
					headerWithName(HttpHeaders.CONTENT_TYPE).description("json 으로 전달")
				),
				responseHeaders(
					headerWithName(HttpHeaders.CONTENT_TYPE).description("json 으로 전달")
				),
				responseFields(
					fieldWithPath("id").type(NUMBER).description("아이디"),
					fieldWithPath("email").type(STRING).description("이메일"),
					fieldWithPath("profileImageUrl").type(STRING).description("프로필 이미지 url"),
					fieldWithPath("nickname").type(STRING).description("닉네임"),
					fieldWithPath("field").type(STRING).description("분야"),
					fieldWithPath("career").type(STRING).description("경력"),
					fieldWithPath("MBTI").type(STRING).description("MBTI"),
					fieldWithPath("githubUrl").type(STRING).description("깃허브 주소"),
					fieldWithPath("blogUrl").type(STRING).description("블로그 주소")
				)));
	}

	private MemberResponse makeMemberResponse() {
		MemberResponse memberResponse = MemberResponse.builder()
			.id(1L)
			.email("test@test.com")
			.profileImageUrl("s3.com")
			.nickname("test")
			.field(Field.ANDROID)
			.career(Career.JUNIOR)
			.MBTI(Mbti.ENFJ)
			.githubUrl("github.com")
			.blogUrl("blog.com")
			.build();
		return memberResponse;
	}
}