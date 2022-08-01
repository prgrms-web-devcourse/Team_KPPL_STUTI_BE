package prgrms.project.stuti.global.error.exception;

import java.text.MessageFormat;
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

	public static MemberException notFoundMember(Long memberId) {
		return new MemberException(ErrorCode.NOT_FOUND_MEMBER,
			MessageFormat.format("회원을 찾을 수 없습니다. (id: {0})", memberId));
	}
}
