package prgrms.project.stuti.global.error.exception;

import java.util.function.Supplier;

import prgrms.project.stuti.global.error.dto.ErrorCode;

public class TokenException extends BusinessException {

	protected TokenException(ErrorCode errorCode, String message) {
		super(errorCode, message);
	}

	protected TokenException(ErrorCode errorCode, String message, Throwable cause) {
		super(errorCode, message, cause);
	}

	public static final Supplier<String> TOKEN_EXPIRATION = () -> {
		throw new TokenException(ErrorCode.TOKEN_EXPIRATION, "token 이 만료되었습니다.");
	};

	public static final Supplier<String> BLACKLIST_DETECTION = () -> {
		throw new TokenException(ErrorCode.BLACKLIST_DETECTION, "이미 logout 된 accessToken 으로의 접근을 감지합니다.");
	};

}
