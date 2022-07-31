package prgrms.project.stuti.global.security;

import static org.springframework.http.HttpHeaders.*;

import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import prgrms.project.stuti.domain.member.model.Email;
import prgrms.project.stuti.domain.member.model.Member;
import prgrms.project.stuti.domain.member.model.MemberRole;
import prgrms.project.stuti.domain.member.service.MemberService;
import prgrms.project.stuti.global.cache.model.RefreshToken;
import prgrms.project.stuti.global.cache.model.TemporaryMember;
import prgrms.project.stuti.global.cache.service.RefreshTokenService;
import prgrms.project.stuti.global.cache.service.TemporaryMemberService;
import prgrms.project.stuti.global.token.TokenGenerator;
import prgrms.project.stuti.global.token.TokenService;
import prgrms.project.stuti.global.token.TokenType;
import prgrms.project.stuti.global.token.Tokens;
import prgrms.project.stuti.global.util.CoderUtil;

@Slf4j
@RequiredArgsConstructor
@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

	private final TokenGenerator tokenGenerator;
	private final TokenService tokenService;
	private final MemberService memberService;
	private final RefreshTokenService refreshTokenService;
	private final TemporaryMemberService temporaryMemberService;
	private RedirectStrategy redirectStratgy = new DefaultRedirectStrategy();

	@Value("${app.oauth.domain}")
	private String domain;

	@Value("${app.oauth.signupPath}")
	private String signupPath;

	@Value("${app.oauth.loginSuccessPath}")
	private String loginSuccessPath;

	@Value("${app.oauth.signupTime}")
	private Long signupTime;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
		Authentication authentication) throws IOException, ServletException {
		// 인증 된 principal 를 가지고 온다.
		OAuth2User oAuth2User = (OAuth2User)authentication.getPrincipal();
		Map<String, Object> attributes = oAuth2User.getAttributes();
		String email = (String)attributes.get("email");
		String name = (String)attributes.get("name");
		String picture = (String)attributes.get("picture");

		Optional<Member> optionalMember = memberService.getMember(new Email(email));
		// 최초 로그인이라면 추가 회원가입 처리를 한다.
		if (optionalMember.isEmpty()) {
			Optional<TemporaryMember> optionalTemporaryMember = temporaryMemberService.findById(email);
			TemporaryMember temporaryMember = TemporaryMember.builder()
				.email(email)
				.nickname(name)
				.imageUrl(picture)
				.expiration(signupTime)
				.build();

			// temporarymember 가 없으면 생성
			if (optionalTemporaryMember.isEmpty()) {
				temporaryMemberService.save(temporaryMember);
			} else {
				// 있으면 기존의 회원가입시도가 있었으므로 그냥 가지고 온다.
				temporaryMember = optionalTemporaryMember.get();
			}

			String temporaryMemberEmail = temporaryMember.getEmail();

			String param1 = "?email=" + CoderUtil.encode(temporaryMemberEmail);
			String param2 = "&name=" + CoderUtil.encode(name);
			String targetURI = domain + signupPath + param1 + param2;
			// 추가 회원가입을 하기 위해 redirect 한다.
			// db 저장후 refreshtoken 저장 한 후 accesstoken 은 쿠키로 전달한다.
			redirectStratgy.sendRedirect(request, response, targetURI);
			return;
		}
		// 이미 회원가입을 한 유저의 경우
		// 1. memberId 로 refreshtoken 이 존재하는지 확인한다.
		// 2. 존재한다면 refreshtoken 을 가지고 accesstoken 을 만든다.
		// 3. 존재하지 않으면 refreshtoken 을 만들고 accesstoken 을 만든다.
		// 4. 쿠키에 accesstoken 을 담아 전달한다.

		Long memberId = optionalMember.get().getId();
		Tokens tokens = tokenGenerator.generateTokens(memberId.toString(), MemberRole.ROLE_MEMBER.name());
		saveRefreshTokenToRedis(memberId, tokens.accessToken(), tokens.refreshToken());
		addAccessTokenToCookie(response, tokens.accessToken(), TokenType.JWT_TYPE);

		redirectStratgy.sendRedirect(request, response, domain + loginSuccessPath);

	}

	private void saveRefreshTokenToRedis(Long memberId, String accessToken, String refreshTokenValue) {
		Date now = new Date();
		RefreshToken refreshToken = RefreshToken.builder()
			.accessTokenValue(accessToken)
			.memberId(memberId)
			.refreshTokenValue(refreshTokenValue)
			.createdTime(now)
			.expirationTime(new Date(now.getTime() + tokenService.getRefreshPeriod()))
			.expiration(tokenService.getRefreshPeriod())
			.build();

		refreshTokenService.save(refreshToken);
	}

	private void addAccessTokenToCookie(HttpServletResponse response, String accessToken, TokenType tokenType) {
		Cookie cookie = new Cookie(AUTHORIZATION, CoderUtil.encode(tokenService.tokenWithType(accessToken, tokenType)));
		cookie.setSecure(true);
		cookie.setHttpOnly(true);
		cookie.setMaxAge((int)tokenService.getAccessTokenPeriod());
		cookie.setPath(loginSuccessPath);

		response.addCookie(cookie);
	}
}
