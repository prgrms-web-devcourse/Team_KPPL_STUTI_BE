package prgrms.project.stuti.domain.feed.service.dto;

public record PostCommentGetDto(
	Long postId,
	Long lastCommentId,
	int size
) {
}
