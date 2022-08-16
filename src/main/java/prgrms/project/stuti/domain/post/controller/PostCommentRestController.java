package prgrms.project.stuti.domain.post.controller;

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
import prgrms.project.stuti.domain.post.controller.dto.PostCommentRequest;
import prgrms.project.stuti.domain.post.service.PostCommentConverter;
import prgrms.project.stuti.domain.post.service.PostCommentService;
import prgrms.project.stuti.domain.post.service.dto.PostCommentChangeDto;
import prgrms.project.stuti.domain.post.service.dto.PostCommentCreateDto;
import prgrms.project.stuti.domain.post.service.dto.PostCommentGetDto;
import prgrms.project.stuti.domain.post.service.dto.PostCommentParentDto;
import prgrms.project.stuti.domain.post.service.response.PostCommentContentsResponse;
import prgrms.project.stuti.domain.post.service.response.PostCommentResponse;
import prgrms.project.stuti.global.page.PageResponse;

@RestController
@RequiredArgsConstructor
public class PostCommentRestController {

	private final PostCommentService postCommentService;

	@PostMapping("/api/v1/posts/{postId}/comments")
	public ResponseEntity<PostCommentResponse> createComment(
		@PathVariable Long postId, @Valid @RequestBody PostCommentRequest postCommentRequest,
		@AuthenticationPrincipal Long memberId
	) {
		PostCommentCreateDto postCommentCreateDto = PostCommentMapper.toPostCommentCreateDto(postCommentRequest, postId,
			memberId);
		PostCommentResponse postCommentResponse = postCommentService.createComment(postCommentCreateDto);

		return ResponseEntity.ok(postCommentResponse);
	}

	@PostMapping("/api/v1/posts/{postId}/comments/{commentId}")
	public ResponseEntity<PostCommentResponse> changeComment(
		@PathVariable Long postId, @PathVariable Long commentId,
		@Valid @RequestBody PostCommentRequest postCommentRequest, @AuthenticationPrincipal Long memberId
		) {
		PostCommentChangeDto postCommentChangeDto = PostCommentMapper.toPostCommentChangeDto(postCommentRequest, postId,
			commentId, memberId);
		PostCommentResponse postCommentResponse = postCommentService.changeComment(postCommentChangeDto);

		return ResponseEntity.ok(postCommentResponse);
	}

	@DeleteMapping("/api/v1/posts/{postId}/comments/{commentId}")
	public ResponseEntity<PostCommentResponse> deleteComment(
		@PathVariable Long postId, @PathVariable Long commentId, @AuthenticationPrincipal Long memberId
	) {
		PostCommentResponse postCommentResponse = postCommentService.deleteComment(postId, commentId, memberId);

		return ResponseEntity.ok(postCommentResponse);
	}

	@GetMapping("/api/v1/posts/{postId}/comments")
	public ResponseEntity<PageResponse<PostCommentParentDto>> getPostComments(
		@PathVariable Long postId, @RequestParam(value = "lastCommentId", required = false) Long lastCommentId,
		@RequestParam(defaultValue = "10") int size
	) {
		PostCommentGetDto postCommentGetDto = PostCommentConverter.toPostCommentGetDto(postId, lastCommentId, size);
		PageResponse<PostCommentParentDto> commentResponse = postCommentService.getPostComments(postCommentGetDto);

		return ResponseEntity.ok(commentResponse);
	}

	@GetMapping("/api/v1/posts/{postId}/comments/{commentId}")
	public ResponseEntity<PostCommentContentsResponse> getCommentContents(
		@PathVariable Long postId, @PathVariable Long commentId
	) {
		PostCommentContentsResponse commentContents = postCommentService.getCommentContents(postId, commentId);

		return ResponseEntity.ok(commentContents);
	}
}
