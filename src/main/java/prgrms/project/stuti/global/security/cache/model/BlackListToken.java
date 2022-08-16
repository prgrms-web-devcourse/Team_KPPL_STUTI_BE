package prgrms.project.stuti.global.security.cache.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import lombok.Getter;

@Getter
@RedisHash(value = "blackListToken")
public class BlackListToken {

	@Id
	private final String tokenInBlackList;

	@TimeToLive
	private final Long expiration;

	public BlackListToken(String tokenInBlackList, Long expiration) {
		this.tokenInBlackList = tokenInBlackList;
		this.expiration = expiration;
	}
}
