package prgrms.project.stuti.global.cache.model;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import lombok.Getter;

@Getter
@RedisHash(value = "refreshToken")
public class RefreshToken {

    @Id
    private String accessTokenValue;

    private String refreshTokenValue;
    private Date createdTime;
    private Date expirationTime;

    @TimeToLive
    private Long expiration;

    public RefreshToken(String accessTokenValue, String refreshTokenValue, Date createdTime,
        Date expirationTime) {
        this.accessTokenValue = accessTokenValue;
        this.refreshTokenValue = refreshTokenValue;
        this.createdTime = createdTime;
        this.expirationTime = expirationTime;
        this.expiration = expirationTime.getTime() - createdTime.getTime();
    }
}
