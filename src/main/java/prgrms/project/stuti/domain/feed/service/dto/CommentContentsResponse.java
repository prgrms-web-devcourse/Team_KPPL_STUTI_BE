package prgrms.project.stuti.domain.feed.service.dto;

public record CommentContentsResponse(
	Long postCommentId,
	Long parentId,
	String contents
) {
}
