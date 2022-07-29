package prgrms.project.stuti.global.token;

import static org.springframework.http.HttpHeaders.*;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import prgrms.project.stuti.global.util.CoderUtil;

@Service
public class TokenService {

	private final long refreshTokenPeriod;
	private final long accessTokenPeriod;

	private String secretKey;
	private byte[] keyBytes;
	private Key key;

	public TokenService(JwtProperties jwtProperties) {
		this.refreshTokenPeriod = jwtProperties.getRefreshTokenExpiry();
		this.accessTokenPeriod = jwtProperties.getTokenExpiry();
		this.secretKey = jwtProperties.getTokenSecret();
		this.secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
		this.keyBytes = secretKey.getBytes();
		this.key = Keys.hmacShaKeyFor(keyBytes);
	}

	public boolean verifyToken(String token) {
		try {
			Jws<Claims> claims = Jwts.parserBuilder()
				.setSigningKey(key)
				.build()
				.parseClaimsJws(token);

			return claims.getBody()
				.getExpiration()
				.after(new Date());
		} catch (Exception e) {
			return false;
		}
	}

	public String[] getRole(String token) {
		return new String[] {
			(String)Jwts.parserBuilder()
				.setSigningKey(key)
				.build()
				.parseClaimsJws(token)
				.getBody()
				.get("roles")
		};
	}

	public String getUid(String token) {
		return Jwts.parserBuilder()
			.setSigningKey(key)
			.build()
			.parseClaimsJws(token)
			.getBody()
			.getSubject();
	}

	public long getExpiration(String token) {
		Date expiration = Jwts.parserBuilder()
			.setSigningKey(key)
			.build()
			.parseClaimsJws(token)
			.getBody()
			.getExpiration();
		Long now = new Date().getTime();

		return (expiration.getTime() - now);
	}

	public String changeToToken(String header) {
		return header.substring("Bearer ".length());
	}

	public long getRefreshPeriod() {
		return refreshTokenPeriod;
	}

	public long getAccessTokenPeriod() {
		return accessTokenPeriod;
	}

	public String tokenWithType(String accessToken, TokenType tokenType) {
		return tokenType.getTypeValue() + accessToken;
	}

	public String resolveToken(HttpServletRequest request) {
		Optional<String> tokenHeader = Optional.ofNullable(((HttpServletRequest)request).getHeader(AUTHORIZATION));
		String token = tokenHeader.map(this::changeToToken).orElse(null);

		return token != null ? CoderUtil.decode(token) : null;
	}

	public void addAccessTokenToCookie(HttpServletResponse response, String accessToken,
		TokenType tokenType) {
		Cookie cookie = setCookie(accessToken, tokenType);
		response.addCookie(cookie);
	}

	private Cookie setCookie(String accessToken, TokenType tokenType) {
		Cookie cookie = new Cookie(AUTHORIZATION, CoderUtil.encode(this.tokenWithType(accessToken, tokenType)));
		cookie.setSecure(true);
		cookie.setHttpOnly(true);
		cookie.setMaxAge((int)this.getAccessTokenPeriod());

		return cookie;
	}
}
