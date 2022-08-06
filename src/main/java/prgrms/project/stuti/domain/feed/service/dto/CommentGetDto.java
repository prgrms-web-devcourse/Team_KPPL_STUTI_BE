package prgrms.project.stuti.domain.feed.service.dto;

public record CommentGetDto(
	Long postId,
	Long lastCommentId,
	int size
) {
}
