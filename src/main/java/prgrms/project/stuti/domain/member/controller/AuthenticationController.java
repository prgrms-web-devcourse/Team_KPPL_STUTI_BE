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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import prgrms.project.stuti.domain.member.service.AuthenticationFacade;
import prgrms.project.stuti.domain.member.service.dto.MemberIdResponse;
import prgrms.project.stuti.domain.member.controller.dto.MemberSaveRequest;
import prgrms.project.stuti.global.token.TokenType;
import prgrms.project.stuti.global.token.Tokens;
import prgrms.project.stuti.global.util.CoderUtil;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AuthenticationController {

	private final AuthenticationFacade authenticationFacade;
	@Value("${app.oauth.domain}")
	private String domain;

	@PostMapping("/signup")
	public ResponseEntity<MemberIdResponse> singup(HttpServletResponse response,
		@Valid @RequestBody MemberSaveRequest memberSaveRequest) {

		MemberIdResponse memberIdResponse = authenticationFacade.signupMember(
			MemberMapper.toMemberDto(memberSaveRequest));
		Tokens tokens = authenticationFacade.makeTokens(memberIdResponse.memberId());

		Cookie cookie = setCookie(tokens.accessToken(), TokenType.JWT_TYPE,
			authenticationFacade.accessTokenPeriod());
		response.addCookie(cookie);

		return ResponseEntity
			.created(URI.create(domain + "/"))
			.body(memberIdResponse);
	}

	@GetMapping("/logout")
	public void logout(HttpServletRequest request, HttpServletResponse response) throws IOException {
		authenticationFacade.logout(request);
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
