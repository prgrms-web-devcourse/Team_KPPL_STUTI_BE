package prgrms.project.stuti.global.cache.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import lombok.Builder;
import lombok.Getter;

@Getter
@RedisHash(value = "temporaryMember")
public class TemporaryMember {

	@Id
	String email;

	String imageUrl;
	String nickname;

	@TimeToLive
	private Long expiration;

	@Builder
	public TemporaryMember(String email, String imageUrl, String nickname, Long expiration) {
		this.email = email;
		this.imageUrl = imageUrl;
		this.nickname = nickname;
		this.expiration = expiration;
	}
}
