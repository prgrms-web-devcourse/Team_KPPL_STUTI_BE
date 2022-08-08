package prgrms.project.stuti.domain.member.controller;

import java.net.URI;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import prgrms.project.stuti.domain.member.controller.dto.MemberIdRequest;
import prgrms.project.stuti.domain.member.controller.dto.MemberSignupResponse;
import prgrms.project.stuti.domain.member.model.Career;
import prgrms.project.stuti.domain.member.model.Field;
import prgrms.project.stuti.domain.member.model.Mbti;
import prgrms.project.stuti.domain.member.model.MemberRole;
import prgrms.project.stuti.domain.member.service.AuthenticationService;
import prgrms.project.stuti.domain.member.controller.dto.MemberSaveRequest;
import prgrms.project.stuti.domain.member.service.dto.MemberResponse;
import prgrms.project.stuti.global.token.TokenService;
import prgrms.project.stuti.global.token.TokenType;
import prgrms.project.stuti.global.token.Tokens;
import prgrms.project.stuti.global.util.CoderUtil;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AuthenticationController {

	private final TokenService tokenService;
	private final AuthenticationService authenticationService;

	@Value("${app.oauth.domain}")
	private String domain;

	// 테스트용 임시 메서드
	@PostMapping("/test/{email}")
	public ResponseEntity<MemberSignupResponse> temporaryMember(@PathVariable String email) {
		MemberSaveRequest memberSaveRequest = MemberSaveRequest.builder()
			.email(email + "@google.com")
			.nickname(email + "user")
			.career(Career.JUNIOR)
			.MBTI(Mbti.ENFJ)
			.field(Field.ANDROID)
			.build();
		MemberResponse memberResponse = authenticationService.testMember(MemberMapper.toMemberDto(memberSaveRequest));
		Long memberId = memberResponse.id();

		Tokens tokens = tokenService.generateTokens(memberId.toString(), MemberRole.ROLE_MEMBER.name());
		authenticationService.saveRefreshToken(memberId, tokens, tokenService.getRefreshPeriod());
		MemberSignupResponse memberSignupResponse = new MemberSignupResponse(memberResponse,
			CoderUtil.encode(tokens.accessToken()));

		URI uri = URI.create(domain);

		return ResponseEntity
			.created(uri)
			.body(memberSignupResponse);

	}

	@PostMapping("/signup")
	public ResponseEntity<MemberSignupResponse> singup(@Valid @RequestBody MemberSaveRequest memberSaveRequest) {
		MemberResponse memberResponse = authenticationService.signupMember(
			MemberMapper.toMemberDto(memberSaveRequest));
		Long memberId = memberResponse.id();

		Tokens tokens = tokenService.generateTokens(memberId.toString(), MemberRole.ROLE_MEMBER.name());
		authenticationService.saveRefreshToken(memberId, tokens, tokenService.getRefreshPeriod());
		MemberSignupResponse memberSignupResponse = new MemberSignupResponse(memberResponse,
			CoderUtil.encode(tokens.accessToken()));

		URI uri = URI.create(domain);

		return ResponseEntity
			.created(uri)
			.body(memberSignupResponse);
	}

	@PostMapping("/login")
	public ResponseEntity<MemberSignupResponse> login(@Valid @RequestBody MemberIdRequest memberIdRequest) {
		Long memberId = memberIdRequest.id();
		Tokens tokens = tokenService.generateTokens(memberId.toString(), MemberRole.ROLE_MEMBER.name());
		authenticationService.saveRefreshToken(memberId, tokens, tokenService.getRefreshPeriod());

		MemberResponse memberResponse = authenticationService.getMemberResponse(memberId);
		MemberSignupResponse memberSignupResponse = new MemberSignupResponse(memberResponse,
			CoderUtil.encode(tokens.accessToken()));

		return ResponseEntity
			.ok()
			.body(memberSignupResponse);
	}

	@PostMapping("/logout")
	public ResponseEntity<Void> logout(HttpServletRequest request) {
		String accessToken = tokenService.resolveToken(request);
		String accessTokenWithType = tokenService.tokenWithType(accessToken, TokenType.JWT_BLACKLIST);
		authenticationService.logout(accessToken, tokenService.getExpiration(accessToken), accessTokenWithType);

		return ResponseEntity.ok()
			.contentType(MediaType.APPLICATION_JSON)
			.build();
	}
}
