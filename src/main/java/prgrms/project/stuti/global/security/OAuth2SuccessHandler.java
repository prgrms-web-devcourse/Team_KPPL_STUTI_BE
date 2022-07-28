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
import prgrms.project.stuti.global.cache.repository.RefreshTokenRepository;
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
	private final RefreshTokenRepository refreshTokenRepository;
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

		Optional<Member> optionalMember = memberService.getUser(new Email(email));
		// 최초 로그인이라면 추가 회원가입 처리를 한다.
		if (!optionalMember.isPresent()) {
			TemporaryMember temporaryMember = new TemporaryMember(email, name, picture, signupTime);
			temporaryMemberService.save(temporaryMember);

			String temporaryMemberEmail = temporaryMember.getEmail();

			String param1 = "?email=" + CoderUtil.encode(temporaryMemberEmail);
			String param2 = "&name=" + CoderUtil.encode(name);
			String targetURI = domain + signupPath + param1 + param2;
			// 추가 회원가입을 하기 위해 redirect 한다.
			redirectStratgy.sendRedirect(request, response, targetURI);
		} else {
			// 이미 회원가입 한 유저의 경우 토큰을 refreshToken 저장 후
			// accessToken 은 쿠키로 담아 main 으로 redirect 한다.
			Tokens tokens = tokenGenerator.generateTokens(email, MemberRole.ROLE_USER.stringValue);
			saveRefreshTokenToRedis(tokens);
			addAccessTokenToCookie(response, tokens.getAccessToken(), TokenType.JWT_TYPE);

			redirectStratgy.sendRedirect(request, response, domain + loginSuccessPath);
		}
	}

	private void saveRefreshTokenToRedis(Tokens tokens) {
		Date now = new Date();
		RefreshToken refreshToken = new RefreshToken(
			tokens.getAccessToken(),
			tokens.getRefreshToken(),
			now,
			new Date(now.getTime() + tokenService.getRefreshPeriod())
		);

		refreshTokenRepository.save(refreshToken);
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
