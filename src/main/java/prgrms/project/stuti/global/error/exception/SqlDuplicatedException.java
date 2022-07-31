package prgrms.project.stuti.global.error.exception;

import java.util.function.Supplier;

import prgrms.project.stuti.global.error.dto.ErrorCode;

public class SqlDuplicatedException  extends BusinessException {

	public SqlDuplicatedException(ErrorCode errorCode, String message) {
		super(errorCode, message);
	}

	public static final Supplier<InputValueException> SQL_DUPLICATED_EXCEPTION = () -> {
		throw new InputValueException(ErrorCode.SQL_DUPLICATED_EXCEPTION, "unique 위반");
	};
}
