package prgrms.project.stuti.domain.feed.service.response;

public record PostCommentContentsResponse(
	Long postCommentId,
	Long parentId,
	String contents
) {
}
