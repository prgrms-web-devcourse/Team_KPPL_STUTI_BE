package prgrms.project.stuti.global.error.dto;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum ErrorCode {

	//common
	INVALID_METHOD_ARGUMENT("C001", "Invalid method argument", HttpStatus.BAD_REQUEST),
	UNKNOWN_SERVER_ERROR("S001", "Unknown server error", HttpStatus.INTERNAL_SERVER_ERROR),

	//study group
	INVALID_STUDY_PERIOD("SG001", "Invalid study period", HttpStatus.BAD_REQUEST),
	NOT_FOUND_STUDY_GROUP("SG002", "Not found study group", HttpStatus.NOT_FOUND),
	NOT_LEADER("SG003", "Not leader", HttpStatus.BAD_REQUEST),

	//file
	EMPTY_FILE("F001", "Uploaded empty file", HttpStatus.BAD_REQUEST),
	UNSUPPORTED_EXTENSION("F002", "Unsupported file extension", HttpStatus.UNSUPPORTED_MEDIA_TYPE),
	OVER_MAX_SIZE("F003", "Over max size", HttpStatus.PAYLOAD_TOO_LARGE),
	FAILED_UPLOAD("F005", "Failed to upload image file", HttpStatus.SERVICE_UNAVAILABLE),
	FAILED_DELETE("F006", "Failed to delete image file", HttpStatus.SERVICE_UNAVAILABLE),

	// Member
	TOKEN_EXPIRATION("M001", "Token is expired", HttpStatus.NOT_FOUND),
	BLACKLIST_DETECTION("M002", "AccessToken is deprived", HttpStatus.NOT_FOUND),
	INVALID_EMAIL("M003", "Email is invalid", HttpStatus.NOT_FOUND),
	NOT_FOUND_MEMBER("M004", "Not found member", HttpStatus.NOT_FOUND);

	private final String code;
	private final String message;
	private final HttpStatus httpStatus;

	ErrorCode(String code, String message, HttpStatus httpStatus) {
		this.code = code;
		this.message = message;
		this.httpStatus = httpStatus;
	}
}
