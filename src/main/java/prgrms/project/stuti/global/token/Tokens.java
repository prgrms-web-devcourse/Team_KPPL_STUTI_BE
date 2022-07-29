package prgrms.project.stuti.global.token;

import lombok.Builder;

public record Tokens(
	String accessToken,
	String refreshToken
) {
	@Builder
	public Tokens(String accessToken, String refreshToken) {
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
	}
}
