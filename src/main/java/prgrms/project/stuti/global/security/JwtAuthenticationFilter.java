package prgrms.project.stuti.global.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import prgrms.project.stuti.global.cache.model.RefreshToken;
import prgrms.project.stuti.global.cache.repository.BlackListTokenRepository;
import prgrms.project.stuti.global.cache.repository.RefreshTokenRepository;
import prgrms.project.stuti.global.error.exception.MemberException;
import prgrms.project.stuti.global.token.TokenService;
import prgrms.project.stuti.global.token.TokenType;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final TokenService tokenService;
	private final RefreshTokenRepository refreshTokenRepository;
	private final BlackListTokenRepository blackListTokenRepository;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {

		String accessToken = tokenService.resolveToken((HttpServletRequest)request);
		boolean isLogout = request.getServletPath().equals("/api/v1/logout");

		// 토큰이 있는지, 유효한지 검증
		if (!isLogout && accessToken != null && tokenService.verifyToken(accessToken)) {
			// 블랙리스트에 존재하는 토큰인지 체크
			checkBlackList(accessToken);

			// 토큰에서 email 과 role 를 가져온다.
			String memberId = tokenService.getUid(accessToken);
			String[] roles = tokenService.getRole(accessToken);

			// ContextHolder 에 저장한 후 다시 accessToken 을 쿠키로 전달
			setAuthenticationToSecurityContextHolder(Long.parseLong(memberId), roles);
			tokenService.addAccessTokenToCookie(response, accessToken, TokenType.JWT_TYPE);

		} else if (!isLogout && accessToken != null) {
			// 토큰이 유효하지 않은경우
			// refresh token 을 redis 에서 찾은 후 존재하는 경우 accessToken 을 재발급하여 제공한다.
			// refresh token 도 존재하지 않은경우 재로그인이 필요하다.
			refreshTokenRepository.findById(accessToken).ifPresent(refreshToken -> {
				String[] role = tokenService.getRole(refreshToken.getRefreshTokenValue());
				Long memberId = refreshToken.getMemberId();

				// accessToken 을 매핑되는 refreshToken 으로 갱신한 후 cookie 에 담은 후 contextholder 에 등록한다.
				String newAccessToken = tokenService.generateAccessToken(memberId.toString(), role);

				refreshTokenRepository.save(RefreshToken.builder()
					.accessTokenValue(newAccessToken)
					.memberId(memberId)
					.refreshTokenValue(refreshToken.getRefreshTokenValue())
					.createdTime(refreshToken.getCreatedTime())
					.expirationTime(refreshToken.getExpirationTime())
					.expiration(refreshToken.getExpiration())
					.build());
				refreshTokenRepository.delete(refreshToken);

				setAuthenticationToSecurityContextHolder(memberId, role);
				tokenService.addAccessTokenToCookie(response, newAccessToken, TokenType.JWT_TYPE);
			});
		}
		// 토큰이 유효하지 않은경우 다음 필터로 이동한다.
		filterChain.doFilter(request, response);
	}

	private void checkBlackList(String token) {
		blackListTokenRepository.findById(tokenService.tokenWithType(token, TokenType.JWT_BLACKLIST))
			.ifPresent(blackListToken -> MemberException.blakclistDetection());
	}

	private void setAuthenticationToSecurityContextHolder(Long memberId, String[] roles) {
		Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
		Arrays.stream(roles).forEach(role -> authorities.add(new SimpleGrantedAuthority(role)));

		MemberIdAuthenticationToken authenticationToken =
			new MemberIdAuthenticationToken(memberId, null, authorities);

		SecurityContextHolder.getContext().setAuthentication(authenticationToken);
	}
}