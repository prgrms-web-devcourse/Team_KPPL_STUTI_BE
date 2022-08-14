package prgrms.project.stuti.global.security.token;

import static org.springframework.http.HttpHeaders.*;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import prgrms.project.stuti.global.security.util.CoderUtil;

@Service
public class TokenService {

	private final String issuer;
	private final long tokenPeriod;
	private final long refreshPeriod;
	private final long refreshTokenPeriod;
	private final long accessTokenPeriod;

	private final String secretKey;
	private final byte[] keyBytes;
	private final Key key;

	public TokenService(JwtProperties jwtProperties) {
		this.refreshTokenPeriod = jwtProperties.getRefreshTokenExpiry();
		this.accessTokenPeriod = jwtProperties.getTokenExpiry();
		this.issuer = jwtProperties.getIssuer();
		this.tokenPeriod = jwtProperties.getTokenExpiry();
		this.refreshPeriod = jwtProperties.getRefreshTokenExpiry();
		this.secretKey = Base64.getEncoder().encodeToString(jwtProperties.getTokenSecret().getBytes());
		this.keyBytes = secretKey.getBytes();
		this.key = Keys.hmacShaKeyFor(keyBytes);
	}

	public Tokens generateTokens(String uid, String roles) {
		Claims claims = Jwts.claims().setSubject(uid);
		claims.put("roles", roles);
		Date now = new Date();

		return new Tokens(generateAccessToken(claims, now), generateRefreshToken(claims, now));
	}

	public String generateAccessToken(Claims claims, Date now) {
		return Jwts.builder()
			.setIssuer(issuer)
			.setClaims(claims)
			.setIssuedAt(now)
			.setExpiration(new Date(now.getTime() + tokenPeriod))
			.signWith(key, SignatureAlgorithm.HS256)
			.compact();
	}

	public String generateAccessToken(String uid, String[] roles) {
		Claims claims = Jwts.claims().setSubject(uid);
		claims.put("roles", roles);
		Date now = new Date();

		return this.generateAccessToken(claims, now);
	}

	public String generateRefreshToken(Claims claims, Date now) {
		return Jwts.builder()
			.setIssuer(issuer)
			.setClaims(claims)
			.setIssuedAt(now)
			.setExpiration(new Date(now.getTime() + refreshPeriod))
			.signWith(key, SignatureAlgorithm.HS256)
			.compact();
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
		Optional<String> tokenHeader = Optional.ofNullable((request).getHeader(AUTHORIZATION));
		String token = tokenHeader.map(this::changeToToken).orElse(null);

		return token != null ? CoderUtil.decode(token) : null;
	}
}
