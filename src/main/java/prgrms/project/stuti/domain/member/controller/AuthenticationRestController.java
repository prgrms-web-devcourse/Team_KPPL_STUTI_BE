package prgrms.project.stuti.domain.member.controller;

import java.net.URI;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import prgrms.project.stuti.domain.member.controller.dto.MemberIdRequest;
import prgrms.project.stuti.domain.member.controller.dto.MemberSignupResponse;
import prgrms.project.stuti.domain.member.model.MemberRole;
import prgrms.project.stuti.domain.member.service.AuthenticationService;
import prgrms.project.stuti.domain.member.controller.dto.MemberSaveRequest;
import prgrms.project.stuti.domain.member.service.dto.MemberResponse;
import prgrms.project.stuti.global.security.token.TokenService;
import prgrms.project.stuti.global.security.token.TokenType;
import prgrms.project.stuti.global.security.token.Tokens;
import prgrms.project.stuti.global.security.util.CoderUtil;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AuthenticationRestController {

	private final TokenService tokenService;
	private final AuthenticationService authenticationService;

	@PostMapping("/signup")
	public ResponseEntity<MemberSignupResponse> singUp(
		HttpServletRequest request, @Valid @RequestBody MemberSaveRequest memberSaveRequest
	) {
		MemberResponse memberResponse = authenticationService.signupMember(MemberMapper.toMemberDto(memberSaveRequest));
		Long memberId = memberResponse.id();

		Tokens tokens = tokenService.generateTokens(memberId.toString(), MemberRole.ROLE_MEMBER.name());
		authenticationService.saveRefreshToken(memberId, tokens, tokenService.getRefreshTokenPeriod());
		MemberSignupResponse memberSignupResponse = new MemberSignupResponse(memberResponse,
			CoderUtil.encode(tokens.accessToken()));

		URI uri = URI.create(request.getScheme() + "://" + request.getHeader(HttpHeaders.HOST));

		return ResponseEntity
			.created(uri)
			.body(memberSignupResponse);
	}

	@PostMapping("/login")
	public ResponseEntity<MemberSignupResponse> login(@Valid @RequestBody MemberIdRequest memberIdRequest) {
		Long memberId = memberIdRequest.id();
		Tokens tokens = tokenService.generateTokens(memberId.toString(), MemberRole.ROLE_MEMBER.name());
		authenticationService.saveRefreshToken(memberId, tokens, tokenService.getRefreshTokenPeriod());

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

	@GetMapping("/auth")
	public ResponseEntity<MemberResponse> memberInfo(HttpServletRequest request) {
		String accessToken = tokenService.resolveToken(request);
		String refreshTokenValue = authenticationService.checkAndGetRefreshToken(accessToken);
		tokenService.verifyRefreshTokenWithException(refreshTokenValue);
		tokenService.verifyAccessTokenWithException(accessToken);
		String memberId = tokenService.getUid(accessToken);
		MemberResponse memberResponse = authenticationService.getMemberResponse(Long.parseLong(memberId));

		return ResponseEntity.ok(memberResponse);
	}
}
