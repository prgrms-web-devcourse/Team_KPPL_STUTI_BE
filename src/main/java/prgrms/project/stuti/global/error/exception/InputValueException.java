package prgrms.project.stuti.global.error.exception;

import java.util.function.Supplier;

import prgrms.project.stuti.global.error.dto.ErrorCode;

public class InputValueException extends BusinessException {

	public InputValueException(ErrorCode errorCode, String message) {
		super(errorCode, message);
	}

	public static final Supplier<InputValueException> EMPTY_INPUT_VALUE = () -> {
		throw new InputValueException(ErrorCode.EMPTY_INPUT_VALUE, "빈 값이 입력되었습니다.");
	};
}
