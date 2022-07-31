package prgrms.project.stuti.global.error.dto;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum ErrorCode {

	UNKNOWN_SERVER_ERROR("s001", "Unknown server error", HttpStatus.INTERNAL_SERVER_ERROR),

	MEMBER_NOT_FOUND("M001", "Unknown member", HttpStatus.BAD_REQUEST);

	private final String code;
	private final String message;
	private final HttpStatus httpStatus;

	ErrorCode(String code, String message, HttpStatus httpStatus) {
		this.code = code;
		this.message = message;
		this.httpStatus = httpStatus;
	}
}
