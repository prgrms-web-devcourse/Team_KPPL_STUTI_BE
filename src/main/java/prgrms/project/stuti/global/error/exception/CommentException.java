package prgrms.project.stuti.global.error.exception;

import prgrms.project.stuti.global.error.dto.ErrorCode;

public class CommentException extends BusinessException{

	protected CommentException(ErrorCode errorCode, String message) {
		super(errorCode, message);
	}

	public static final CommentException PARENT_COMMENT_NOT_FOUND() {
		throw new CommentException(ErrorCode.PARENT_COMMENT_NOT_FOUND, "원 댓글이 존재하지 않습니다.");
	}
}
