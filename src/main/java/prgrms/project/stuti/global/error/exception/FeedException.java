package prgrms.project.stuti.global.error.exception;

import prgrms.project.stuti.global.error.dto.ErrorCode;

public class FeedException extends BusinessException{

	protected FeedException(ErrorCode errorCode, String message) {
		super(errorCode, message);
	}

	public static final FeedException FEED_NOT_FOUND() {
		throw new FeedException(ErrorCode.FEED_NOT_FOUND, "존재하지 않는 피드입니다.");
	}
}
