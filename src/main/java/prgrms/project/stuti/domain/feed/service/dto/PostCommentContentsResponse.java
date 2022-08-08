package prgrms.project.stuti.domain.feed.service.dto;

public record PostCommentContentsResponse(
	Long postCommentId,
	Long parentId,
	String contents
) {
}
