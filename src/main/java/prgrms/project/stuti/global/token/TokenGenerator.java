package prgrms.project.stuti.global.token;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class TokenGenerator {

	private final String issuer;
	private final long tokenPeriod;
	private final long refreshPeriod;

	private String secretKey;
	private byte[] keyBytes;
	private Key key;

	public TokenGenerator(JwtProperties jwtProperties) {
		this.issuer = jwtProperties.getIssuer();
		this.tokenPeriod = jwtProperties.getTokenExpiry();
		this.refreshPeriod = jwtProperties.getRefreshTokenExpiry();
		this.secretKey = jwtProperties.getTokenSecret();
		secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
		this.keyBytes = secretKey.getBytes();
		this.key = Keys.hmacShaKeyFor(keyBytes);
	}

	public Tokens generateTokens(String uid, String role) {
		Claims claims = Jwts.claims().setSubject(uid);
		claims.put("role", role);
		Date now = new Date();

		return new Tokens(
			generateAccessToken(claims, now),
			generateRefreshToken(claims, now)
		);
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

	public String generateAccessToken(String uid, String[] role) {
		Claims claims = Jwts.claims().setSubject(uid);
		claims.put("role", role);
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

}
