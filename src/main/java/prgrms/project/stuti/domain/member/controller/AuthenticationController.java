package prgrms.project.stuti.domain.member.controller;

import java.io.IOException;
import java.net.URI;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import prgrms.project.stuti.domain.member.model.MemberRole;
import prgrms.project.stuti.domain.member.service.AuthenticationService;
import prgrms.project.stuti.domain.member.service.dto.MemberIdResponse;
import prgrms.project.stuti.domain.member.controller.dto.MemberSaveRequest;
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

	@PostMapping("/signup")
	public ResponseEntity<MemberIdResponse> singup(HttpServletResponse response,
		@Valid @RequestBody MemberSaveRequest memberSaveRequest) {
		MemberIdResponse memberIdResponse = authenticationService.signupMember(
			MemberMapper.toMemberDto(memberSaveRequest));
		Long memberId = memberIdResponse.memberId();

		Tokens tokens = tokenService.generateTokens(memberId.toString(), MemberRole.ROLE_MEMBER.name());
		authenticationService.saveRefreshToken(memberId, tokens, tokenService.getRefreshPeriod());

		Cookie cookie = setCookie(tokens.accessToken(), TokenType.JWT_TYPE,
			tokenService.getAccessTokenPeriod());
		response.addCookie(cookie);
		URI uri = URI.create(domain + "/");

		return ResponseEntity
			.created(uri)
			.body(memberIdResponse);
	}

	@PostMapping("/logout")
	public void logout(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String accessToken = tokenService.resolveToken(request);
		String accessTokenWithType = tokenService.tokenWithType(accessToken, TokenType.JWT_BLACKLIST);
		authenticationService.logout(accessToken, tokenService.getExpiration(accessToken), accessTokenWithType);

		response.sendRedirect(domain + "/");
	}

	private Cookie setCookie(String accessToken, TokenType tokenType, long period) {
		Cookie cookie = new Cookie(HttpHeaders.AUTHORIZATION, CoderUtil.encode(tokenType.getTypeValue() + accessToken));
		cookie.setSecure(true);
		cookie.setHttpOnly(true);
		cookie.setMaxAge((int)period);

		return cookie;
	}
}
