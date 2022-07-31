package prgrms.project.stuti.global.error.exception;

import java.util.function.Supplier;

import prgrms.project.stuti.global.error.dto.ErrorCode;

public class NotFoundException extends BusinessException {

	public NotFoundException(ErrorCode errorCode, String message) {
		super(errorCode, message);
	}

	public static final Supplier<String> MEMBER_NOT_FOUND = () -> {
		throw new NotFoundException(ErrorCode.MEMBER_NOT_FOUND, "존재하지 않는 멤버입니다.");
	};
}
