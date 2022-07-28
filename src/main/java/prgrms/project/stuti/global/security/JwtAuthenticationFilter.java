package prgrms.project.stuti.global.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import prgrms.project.stuti.global.cache.model.BlackListToken;
import prgrms.project.stuti.global.cache.model.RefreshToken;
import prgrms.project.stuti.global.cache.repository.RefreshTokenRepository;
import prgrms.project.stuti.global.cache.service.BlackListTokenService;
import prgrms.project.stuti.global.error.exception.TokenException;
import prgrms.project.stuti.global.token.TokenGenerator;
import prgrms.project.stuti.global.token.TokenService;
import prgrms.project.stuti.global.token.TokenType;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final TokenService tokenService;
	private final TokenGenerator tokenGenerator;
	private final RefreshTokenRepository refreshTokenRepository;
	private final BlackListTokenService blackListTokenService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {

		String token = tokenService.resolveToken((HttpServletRequest)request);

		// 토큰이 있는지, 유효한지 검증
		if (token != null && tokenService.verifyToken(token)) {
			// 블랙리스트에 존재하는 토큰인지 체크
			checkBlackList(token);

			// 토큰에서 email 과 role 를 가져온다.
			String email = tokenService.getUid(token);
			String[] roles = tokenService.getRole(token);

			setAuthenticationToSecurityContextHolder(email, roles);
			tokenService.addAccessTokenToCookie(response, token, TokenType.JWT_TYPE);

		} else if (token != null) {
			// 토큰이 유효하지 않은경우
			// refresh token 을 redis 에서 찾은 후 존재하는 경우 accessToken 을 재발급하여 제공한다.
			Optional<RefreshToken> optionalRefreshToken = refreshTokenRepository.findById(token);

			if (optionalRefreshToken.isPresent()) {
				RefreshToken refreshToken = optionalRefreshToken.get();
				String email = tokenService.getUid(refreshToken.getRefreshTokenValue());
				String[] role = tokenService.getRole(refreshToken.getRefreshTokenValue());

				// accessToken 을 매핑되는 refreshToken 으로 갱신한 후 cookie 에 담은 후 contextholder 에 등록한다.
				String newAccessToken = tokenGenerator.generateAccessToken(email, role);
				setAuthenticationToSecurityContextHolder(email, role);
				tokenService.addAccessTokenToCookie(response, newAccessToken, TokenType.JWT_TYPE);
			}
		}
		// 토큰이 유효하지 않은경우 다음 필터로 이동한다.
		filterChain.doFilter(request, response);
	}

	private void checkBlackList(String token) {
		Optional<BlackListToken> blackListToken = blackListTokenService.findById(
			tokenService.tokenWithType(token, TokenType.JWT_BLACKLIST));
		if (blackListToken.isPresent()) {
			TokenException.BLACKLIST_DETECTION.get();
		}
	}

	private void setAuthenticationToSecurityContextHolder(String email, String[] roles) {
		Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
		Arrays.stream(roles).forEach(role -> {
			authorities.add(new SimpleGrantedAuthority(role));
		});

		UsernamePasswordAuthenticationToken authenticationToken =
			new UsernamePasswordAuthenticationToken(email, null, authorities);

		SecurityContextHolder.getContext().setAuthentication(authenticationToken);
	}
}