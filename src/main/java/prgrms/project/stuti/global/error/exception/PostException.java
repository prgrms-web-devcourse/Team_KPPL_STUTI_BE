package prgrms.project.stuti.global.error.exception;

import prgrms.project.stuti.global.error.dto.ErrorCode;

public class PostException extends BusinessException{

	protected PostException(ErrorCode errorCode, String message) {
		super(errorCode, message);
	}

	public static final PostException POST_NOT_FOUND() {
		throw new PostException(ErrorCode.POST_NOT_FOUND, "존재하지 않는 게시글입니다.");
	}

	public static final PostException POST_LIKE_DUPLICATED() {
		throw new PostException(ErrorCode.POST_LIKE_DUPLICATED, "이미 좋아요를 누른 게시글입니다.");
	}

	public static final PostException NOT_FOUND_POST_LIKE() {
		throw new PostException(ErrorCode.NOT_FOUND_POST_LIKE, "존재하지 않는 좋아요 입니다.");
	}

	public static final PostException INVALID_EDITOR() {
		throw new PostException(ErrorCode.INVALID_EDITOR, "작성자와 수정자가 다릅니다.");
	}
}
