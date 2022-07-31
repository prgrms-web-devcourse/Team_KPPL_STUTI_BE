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

	public static final Supplier<MemberException> INVALID_EMAIL = () -> {
		throw new MemberException(ErrorCode.INVALID_EMAIL, "이메일이 유효하지 않습니다.");
	};

	public static final Supplier<MemberException> NOT_FOUNT_MEMBER = () -> {
		throw new MemberException(ErrorCode.NOT_FOUNT_MEMBER, "멤버를 찾지 못했습니다.");
	};
}
