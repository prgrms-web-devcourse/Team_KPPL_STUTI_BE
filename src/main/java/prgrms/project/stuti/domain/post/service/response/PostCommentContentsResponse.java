package prgrms.project.stuti.domain.post.service.response;

public record PostCommentContentsResponse(
	Long postCommentId,
	Long parentId,
	String contents
) {
}
