package prgrms.project.stuti.global.cache.model;

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
	private String accessTokenValue;

	private String refreshTokenValue;
	private Long memberId;
	private Date createdTime;
	private Date expirationTime;

	@TimeToLive
	private Long expiration;

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
