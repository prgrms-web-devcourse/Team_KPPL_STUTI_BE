package prgrms.project.stuti.domain.feed.controller;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import prgrms.project.stuti.domain.feed.controller.dto.PostCommentRequest;
import prgrms.project.stuti.domain.feed.service.PostCommentConverter;
import prgrms.project.stuti.domain.feed.service.PostCommentService;
import prgrms.project.stuti.domain.feed.service.dto.CommentParentContents;
import prgrms.project.stuti.domain.feed.service.response.PostCommentContentsResponse;
import prgrms.project.stuti.domain.feed.service.dto.PostCommentCreateDto;
import prgrms.project.stuti.domain.feed.service.dto.PostCommentGetDto;
import prgrms.project.stuti.domain.feed.service.response.PostCommentResponse;
import prgrms.project.stuti.domain.feed.service.dto.PostCommentUpdateDto;
import prgrms.project.stuti.global.page.offset.PageResponse;

@RestController
@RequiredArgsConstructor
public class PostCommentController {

	private final PostCommentService postCommentService;

	@PostMapping("/api/v1/posts/{postId}/comments")
	public ResponseEntity<PostCommentResponse> createComment(@PathVariable Long postId,
		@Valid @RequestBody PostCommentRequest postCommentRequest, @AuthenticationPrincipal Long memberId) {
		PostCommentCreateDto postCommentCreateDto = PostCommentMapper.toPostCommentCreateDto(postCommentRequest, postId,
			memberId);
		PostCommentResponse postCommentResponse = postCommentService.createComment(postCommentCreateDto);

		return ResponseEntity.ok(postCommentResponse);
	}

	@PostMapping("/api/v1/posts/{postId}/comments/{commentId}")
	public ResponseEntity<PostCommentResponse> changeComment(@PathVariable Long postId, @PathVariable Long commentId,
		@Valid @RequestBody PostCommentRequest postCommentRequest, @AuthenticationPrincipal Long memberId) {
		PostCommentUpdateDto postCommentUpdateDto = PostCommentMapper.toPostCommentUpdateDto(postCommentRequest, postId,
			commentId, memberId);
		PostCommentResponse postCommentResponse = postCommentService.changeComment(postCommentUpdateDto);

		return ResponseEntity.ok().body(postCommentResponse);
	}

	@DeleteMapping("/api/v1/posts/{postId}/comments/{commentId}")
	public ResponseEntity<PostCommentResponse> deleteComment(@PathVariable Long postId, @PathVariable Long commentId,
		@AuthenticationPrincipal Long memberId) {
		PostCommentResponse postCommentResponse = postCommentService.deleteComment(postId, commentId, memberId);

		return ResponseEntity.ok(postCommentResponse);
	}

	@GetMapping("/api/v1/posts/{postId}/comments")
	public ResponseEntity<PageResponse<CommentParentContents>> getPostComments(@PathVariable Long postId,
		@RequestParam(value = "lastCommentId", required = false) Long lastCommentId,
		@RequestParam(defaultValue = "10") int size) {
		PostCommentGetDto postCommentGetDto = PostCommentConverter.toPostCommentGetDto(postId, lastCommentId, size);
		PageResponse<CommentParentContents> commentResponse = postCommentService.getPostComments(postCommentGetDto);

		return ResponseEntity.ok().body(commentResponse);
	}

	@GetMapping("/api/v1/posts/{postId}/comments/{commentId}")
	public ResponseEntity<PostCommentContentsResponse> getCommentContents(
		@PathVariable Long postId, @PathVariable Long commentId
	) {
		PostCommentContentsResponse commentContents = postCommentService.getCommentContents(postId, commentId);

		return ResponseEntity.ok().body(commentContents);
	}
}
