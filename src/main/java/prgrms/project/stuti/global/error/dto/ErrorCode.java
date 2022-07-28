package prgrms.project.stuti.global.error.dto;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum ErrorCode {

	// Common
	UNKNOWN_SERVER_ERROR("s001", "Unknown server error", HttpStatus.INTERNAL_SERVER_ERROR),

	// Member
	TOKEN_EXPIRATION("M001", "Token is expired", HttpStatus.NOT_FOUND),
	BLACKLIST_DETECTION("M002", "AccessToken is deprived", HttpStatus.NOT_FOUND),
	INVALID_EMAIL("M003", "Email is invalid", HttpStatus.NOT_FOUND);

	private final String code;
	private final String message;
	private final HttpStatus httpStatus;

	ErrorCode(String code, String message, HttpStatus httpStatus) {
		this.code = code;
		this.message = message;
		this.httpStatus = httpStatus;
	}
}
