package prgrms.project.stuti.global.token;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;

@ConfigurationProperties(prefix = "app.jwt")
@Component
@Getter
public class JwtProperties {

	private String header;
	private String issuer;
	private String tokenSecret;
	private long tokenExpiry;
	private long refreshTokenExpiry;

	public void setHeader(String header) {
		this.header = header;
	}

	public void setIssuer(String issuer) {
		this.issuer = issuer;
	}

	public void setTokenSecret(String tokenSecret) {
		this.tokenSecret = tokenSecret;
	}

	public void setTokenExpiry(long tokenExpiry) {
		this.tokenExpiry = tokenExpiry;
	}

	public void setRefreshTokenExpiry(long refreshTokenExpiry) {
		this.refreshTokenExpiry = refreshTokenExpiry;
	}
}
