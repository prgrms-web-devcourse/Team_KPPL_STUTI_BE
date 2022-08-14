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

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import prgrms.project.stuti.global.security.cache.model.RefreshToken;
import prgrms.project.stuti.global.security.cache.repository.BlackListTokenRepository;
import prgrms.project.stuti.global.security.cache.repository.RefreshTokenRepository;
import prgrms.project.stuti.global.error.dto.ErrorCode;
import prgrms.project.stuti.global.error.dto.TokenExpirationResponse;
import prgrms.project.stuti.global.error.exception.MemberException;
import prgrms.project.stuti.global.security.token.TokenService;
import prgrms.project.stuti.global.security.token.TokenType;
import prgrms.project.stuti.global.security.util.CoderUtil;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final TokenService tokenService;
	private final RefreshTokenRepository refreshTokenRepository;
	private final BlackListTokenRepository blackListTokenRepository;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {

		String accessToken = tokenService.resolveToken(request);
		boolean isLogout = request.getServletPath().equals("/api/v1/logout");
		ObjectMapper objectMapper = new ObjectMapper();

		// 1. accessToken 이 유효한경우
		if (!isLogout && accessToken != null && tokenService.verifyToken(accessToken)) {
			checkBlackList(accessToken);

			String memberId = tokenService.getUid(accessToken);
			String[] roles = tokenService.getRole(accessToken);

			setAuthenticationToSecurityContextHolder(Long.parseLong(memberId), roles);
			filterChain.doFilter(request, response);
			return;
		} else if (!isLogout && accessToken != null) {
			// 2. accessToken 에 해당하는 refreshToken 이 존재하지 않은 경우 : T002
			Optional<RefreshToken> optionalRefreshToken = refreshTokenRepository.findById(accessToken);
			if (optionalRefreshToken.isEmpty()) {
				refreshTokenExpirationException(response, objectMapper);
				return;
			}

			RefreshToken refreshToken = optionalRefreshToken.get();
			String refreshTokenValue = refreshToken.getRefreshTokenValue();
			// 3. accessToken 에 해당하는 refreshToken 이 존재하고 유효한 경우 : T001
			if (tokenService.verifyToken(refreshTokenValue)) {
				String[] role = tokenService.getRole(refreshToken.getRefreshTokenValue());
				Long memberId = refreshToken.getMemberId();
				String newAccessToken = tokenService.generateAccessToken(memberId.toString(), role);

				refreshTokenRepository.save(makeRefreshToken(refreshToken, memberId, newAccessToken));
				refreshTokenRepository.delete(refreshToken);

				accessTokenExpirationException(response, objectMapper, CoderUtil.encode(newAccessToken));
				return;
			}
			// 4. refreshtoken 이 존재하는데 유효하지 않은경우 : T002
			refreshTokenExpirationException(response, objectMapper);
			return;
		}
		filterChain.doFilter(request, response);
	}

	private void accessTokenExpirationException(HttpServletResponse response, ObjectMapper objectMapper,
		String newAccessToken) throws IOException {
		response.setStatus(HttpStatus.UNAUTHORIZED.value());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setCharacterEncoding("UTF-8");
		objectMapper.writeValue(response.getWriter(),
			TokenExpirationResponse.of(ErrorCode.ACCESS_TOKEN_EXPIRATION, newAccessToken));
	}

	private void refreshTokenExpirationException(HttpServletResponse response, ObjectMapper objectMapper) throws
		IOException {
		response.setStatus(HttpStatus.UNAUTHORIZED.value());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setCharacterEncoding("UTF-8");
		objectMapper.writeValue(response.getWriter(), TokenExpirationResponse.of(ErrorCode.REFRESH_TOKEN_EXPIRATION));
	}

	private RefreshToken makeRefreshToken(RefreshToken refreshToken, Long memberId, String newAccessToken) {
		return RefreshToken.builder()
			.accessTokenValue(newAccessToken)
			.memberId(memberId)
			.refreshTokenValue(refreshToken.getRefreshTokenValue())
			.createdTime(refreshToken.getCreatedTime())
			.expirationTime(refreshToken.getExpirationTime())
			.expiration(refreshToken.getExpiration())
			.build();
	}

	private void checkBlackList(String token) {
		blackListTokenRepository.findById(tokenService.tokenWithType(token, TokenType.JWT_BLACKLIST))
			.ifPresent(blackListToken -> {
				throw MemberException.blacklistDetection();
			});
	}

	private void setAuthenticationToSecurityContextHolder(Long memberId, String[] roles) {
		Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
		Arrays.stream(roles).forEach(role -> authorities.add(new SimpleGrantedAuthority(role)));

		MemberIdAuthenticationToken authenticationToken =
			new MemberIdAuthenticationToken(memberId, null, authorities);

		SecurityContextHolder.getContext().setAuthentication(authenticationToken);
	}
}