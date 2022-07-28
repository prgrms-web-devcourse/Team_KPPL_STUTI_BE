package prgrms.project.stuti.global.error.exception;

import lombok.Getter;
import prgrms.project.stuti.global.error.dto.ErrorCode;

@Getter
public class BusinessException extends RuntimeException {

	private final ErrorCode errorCode;

	protected BusinessException(ErrorCode errorCode, String message) {
		super(message);
		this.errorCode = errorCode;
	}

	protected BusinessException(ErrorCode errorCode, String message, Throwable cause) {
		super(message, cause);
		this.errorCode = errorCode;
	}
}
