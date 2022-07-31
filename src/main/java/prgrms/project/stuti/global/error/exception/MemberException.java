package prgrms.project.stuti.global.error.exception;

import java.util.function.Supplier;

import prgrms.project.stuti.global.error.dto.ErrorCode;

public class MemberException extends BusinessException {

	protected MemberException(ErrorCode errorCode, String message) {
		super(errorCode, message);
	}

	protected MemberException(ErrorCode errorCode, String message, Throwable cause) {
		super(errorCode, message, cause);
	}

	public static final Supplier<String> INVALID_EMAIL = () -> {
		throw new TokenException(ErrorCode.INVALID_EMAIL, "이메일이 유효하지 않습니다.");
	};
}
