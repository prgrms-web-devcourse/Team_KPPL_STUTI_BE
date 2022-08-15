package prgrms.project.stuti.domain.post.service.dto;

public record PostCommentGetDto(
	Long postId,
	Long lastCommentId,
	int size
) {
}
