package prgrms.project.stuti.global.error.exception;

import prgrms.project.stuti.global.error.dto.ErrorCode;

public class NotFoundException extends BusinessException {

	public NotFoundException(ErrorCode errorCode, String message) {
		super(errorCode, message);
	}
}
