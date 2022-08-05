package prgrms.project.stuti.global.error.exception;

import prgrms.project.stuti.global.error.dto.ErrorCode;

public class FeedException extends BusinessException{

	protected FeedException(ErrorCode errorCode, String message) {
		super(errorCode, message);
	}

	public static final FeedException FEED_NOT_FOUND() {
		throw new FeedException(ErrorCode.FEED_NOT_FOUND, "존재하지 않는 게시글입니다.");
	}

	public static final FeedException FEED_LIKE_DUPLICATED() {
		throw new FeedException(ErrorCode.FEED_LIKE_DUPLICATED, "이미 좋아요를 누른 게시글입니다.");
	}

	public static final FeedException NOT_FOUND_FEED_LIKE() {
		throw new FeedException(ErrorCode.NOT_FOUND_FEED_LIKE, "존재하지 않는 좋아요 입니다.");
	}
}
