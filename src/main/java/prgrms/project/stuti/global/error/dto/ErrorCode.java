package prgrms.project.stuti.global.error.dto;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum ErrorCode {

	//common
	EMPTY_INPUT_VALUE("C001", "Empty request value", HttpStatus.BAD_REQUEST),

	//thumbnailFile
	EMPTY_FILE("F001", "Uploaded empty file", HttpStatus.BAD_REQUEST),
	UNSUPPORTED_EXTENSION("F002", "Unsupported file extension", HttpStatus.UNSUPPORTED_MEDIA_TYPE),
	OVER_MAX_SIZE("F003", "Over max size", HttpStatus.PAYLOAD_TOO_LARGE),
	FAILED_RESIZE("F004", "Failed to resize image file", HttpStatus.SERVICE_UNAVAILABLE),
	FAILED_UPLOAD("F005", "Failed to upload image file", HttpStatus.SERVICE_UNAVAILABLE),
	FAILED_DELETE("F006", "Failed to delete image file", HttpStatus.SERVICE_UNAVAILABLE),

	//server
	UNKNOWN_SERVER_ERROR("s001", "Unknown server error", HttpStatus.INTERNAL_SERVER_ERROR);

	private final String code;
	private final String message;
	private final HttpStatus httpStatus;

	ErrorCode(String code, String message, HttpStatus httpStatus) {
		this.code = code;
		this.message = message;
		this.httpStatus = httpStatus;
	}
}
