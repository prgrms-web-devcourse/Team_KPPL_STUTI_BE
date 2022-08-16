package prgrms.project.stuti.global.security.cache.model;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import lombok.Builder;
import lombok.Getter;

@Getter
@RedisHash(value = "refreshToken")
public class RefreshToken {

	@Id
	private final String accessTokenValue;

	private final String refreshTokenValue;
	private final Long memberId;
	private final Date createdTime;
	private final Date expirationTime;

	@TimeToLive
	private final Long expiration;

	@Builder
	public RefreshToken(String accessTokenValue, String refreshTokenValue, Long memberId, Date createdTime,
		Date expirationTime, Long expiration) {
		this.accessTokenValue = accessTokenValue;
		this.refreshTokenValue = refreshTokenValue;
		this.memberId = memberId;
		this.createdTime = createdTime;
		this.expirationTime = expirationTime;
		this.expiration = expiration;
	}
}
