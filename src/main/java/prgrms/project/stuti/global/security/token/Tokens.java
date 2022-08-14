package prgrms.project.stuti.global.security.token;

import lombok.Builder;

public record Tokens(
	String accessToken,
	String refreshToken
) {
	@Builder
	public Tokens {
	}
}
