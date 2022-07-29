package prgrms.project.stuti.global.cache.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import lombok.Getter;

@Getter
@RedisHash(value = "blackListToken")
public class BlackListToken {

	@Id
	private String blackListToken;

	@TimeToLive
	private Long expiration;

	public BlackListToken(String blackListToken, Long expiration) {
		this.blackListToken = blackListToken;
		this.expiration = expiration;
	}
}
