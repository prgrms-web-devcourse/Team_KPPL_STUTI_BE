package prgrms.project.stuti.global.error.exception;

import lombok.Getter;
import prgrms.project.stuti.global.error.dto.ErrorCode;

@Getter
public class TokenException extends RuntimeException {
	private final ErrorCode errorCode;

	protected TokenException(ErrorCode errorCode, String tokenValue) {
		super(tokenValue);
		this.errorCode = errorCode;
	}

	public static TokenException accessTokenExpiration(String token) {
		return new TokenException(ErrorCode.ACCESS_TOKEN_EXPIRATION, token);
	}

	public static TokenException refreshTokenExpiration(String token) {
		return new TokenException(ErrorCode.REFRESH_TOKEN_EXPIRATION, token);
	}
}
