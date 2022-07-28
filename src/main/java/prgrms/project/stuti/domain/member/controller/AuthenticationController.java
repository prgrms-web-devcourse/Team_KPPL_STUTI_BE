package prgrms.project.stuti.domain.member.controller;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import prgrms.project.stuti.domain.member.service.dto.MemberResponse;
import prgrms.project.stuti.domain.member.controller.dto.MemberSaveRequest;
import prgrms.project.stuti.domain.member.controller.mapper.MemberMapper;
import prgrms.project.stuti.domain.member.model.Member;
import prgrms.project.stuti.domain.member.model.MemberRole;
import prgrms.project.stuti.domain.member.service.MemberService;
import prgrms.project.stuti.global.cache.model.TemporaryMember;
import prgrms.project.stuti.global.cache.service.BlackListTokenService;
import prgrms.project.stuti.global.cache.service.RefreshTokenService;
import prgrms.project.stuti.global.cache.service.TemporaryMemberService;
import prgrms.project.stuti.global.error.exception.TokenException;
import prgrms.project.stuti.global.token.TokenGenerator;
import prgrms.project.stuti.global.token.TokenService;
import prgrms.project.stuti.global.token.TokenType;
import prgrms.project.stuti.global.token.Tokens;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AuthenticationController {

	private final MemberService memberService;
	private final TokenService tokenService;
	private final BlackListTokenService blackListTokenService;
	private final RefreshTokenService refreshTokenService;
	private final TemporaryMemberService temporaryMemberService;
	private final TokenGenerator tokenGenerator;

	@PostMapping("/signup")
	public ResponseEntity<MemberResponse> singup(
		HttpServletResponse response,
		@Valid @RequestBody MemberSaveRequest memberSaveRequest
	) {
		Optional<TemporaryMember> optionalMember = temporaryMemberService.findById(memberSaveRequest.email());

		if (optionalMember.isEmpty()) {
			TokenException.TOKEN_EXPIRATION.get();
		}

		TemporaryMember temporaryMember = optionalMember.get();
		MemberResponse memberResponse = memberService.signup(MemberMapper.toMemberDto(memberSaveRequest),
			temporaryMember);

		// 생성된 refreshToken 저장 후
		Tokens tokens = tokenGenerator.generateTokens(memberResponse.memberId().toString(),
			MemberRole.ROLE_USER.stringValue);
		refreshTokenService.save(tokens, tokenService.getRefreshPeriod());

		// 생성된 accessToken 쿠키 전달
		String accessToken = tokens.getAccessToken();
		tokenService.addAccessTokenToCookie(response, accessToken, TokenType.JWT_TYPE);

		return ResponseEntity
			.created(URI.create("/api/v1/main"))
			.body(memberResponse);
	}

	@GetMapping("/logout")
	public String logout(HttpServletRequest request) {
		String token = tokenService.resolveToken(request);

		if (token != null) {
			long expiration = tokenService.getExpiration(token);

			refreshTokenService.findAndDelete(token);
			blackListTokenService.logout(tokenService.tokenWithType(token, TokenType.JWT_BLACKLIST), expiration);
		}

		return "redirect:/main";
	}

	@GetMapping("/users")
	public ResponseEntity<List<Member>> getUsers() {
		// 테스트 용도입니다.
		return ResponseEntity
			.ok()
			.body(memberService.getUsers());
	}
}
