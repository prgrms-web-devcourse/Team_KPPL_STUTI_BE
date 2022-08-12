package prgrms.project.stuti.global.error.exception;

import java.text.MessageFormat;

import prgrms.project.stuti.global.error.dto.ErrorCode;

public class PostException extends BusinessException {

	protected PostException(ErrorCode errorCode, String message) {
		super(errorCode, message);
	}

	public static PostException POST_NOT_FOUND(Long postId) {
		throw new PostException(ErrorCode.NOT_FOUND_POST,
			MessageFormat.format("존재하지 않는 게시글입니다. (postId: {0})", postId));
	}

	public static PostException POST_LIKE_DUPLICATED(Long postLikeId) {
		throw new PostException(ErrorCode.POST_LIKE_DUPLICATED,
			MessageFormat.format("이미 좋아요를 누른 게시글입니다. (postLikeId: {0})", postLikeId));
	}

	public static PostException NOT_FOUND_POST_LIKE(Long postId, Long memberId) {
		throw new PostException(ErrorCode.NOT_FOUND_POST_LIKE,
			MessageFormat.format("좋아요를 누르지 않은 게시글은 좋아요를 취소할 수 없습니다. (postId: {0}, memberId: {1})",
				postId, memberId));
	}

	public static PostException INVALID_EDITOR(Long creatorId, Long editorId) {
		throw new PostException(ErrorCode.INVALID_EDITOR,
			MessageFormat.format("작성자와 수정자가 같지 않으면 삭제 할 수 없습니다. (creatorId: {0}, editorId: {1})",
				creatorId, editorId));
	}

	public static PostException PARENT_COMMENT_NOT_FOUND(Long parentPostCommentId) {
		throw new PostException(ErrorCode.NOT_FOUND_PARENT_POST_COMMENT,
			MessageFormat.format("상위 댓글이 존재하지 않습니다. (parentPostCommentId: {0})", parentPostCommentId));
	}

	public static PostException COMMENT_NOT_FOUND(Long postCommentId) {
		throw new PostException(ErrorCode.NOT_FOUND_POST_COMMENT,
			MessageFormat.format("댓글이 존재하지 않습니다. (postCommentId: {0})", postCommentId));
	}
}
