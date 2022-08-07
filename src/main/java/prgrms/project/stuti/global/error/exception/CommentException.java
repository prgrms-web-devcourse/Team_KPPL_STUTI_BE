package prgrms.project.stuti.global.error.exception;

import java.text.MessageFormat;

import prgrms.project.stuti.global.error.dto.ErrorCode;

public class CommentException extends BusinessException{

	protected CommentException(ErrorCode errorCode, String message) {
		super(errorCode, message);
	}

	public static final CommentException PARENT_COMMENT_NOT_FOUND(Long postCommentId) {
		throw new CommentException(ErrorCode.PARENT_COMMENT_NOT_FOUND,
			MessageFormat.format("상위 댓글이 존재하지 않습니다. ({0})", postCommentId));
	}

	public static final CommentException COMMENT_NOT_FOUND(Long postCommentId) {
		throw new CommentException(ErrorCode.COMMENT_NOT_FOUND,
			MessageFormat.format("댓글이 존재하지 않습니다. ({0})", postCommentId));
	}
}
