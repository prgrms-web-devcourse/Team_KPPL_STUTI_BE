package prgrms.project.stuti.domain.feed.controller;

import java.net.URI;

import javax.validation.Valid;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import prgrms.project.stuti.domain.feed.controller.dto.CommentRequest;
import prgrms.project.stuti.domain.feed.service.CommentConverter;
import prgrms.project.stuti.domain.feed.service.CommentService;
import prgrms.project.stuti.domain.feed.service.dto.CommentCreateDto;
import prgrms.project.stuti.domain.feed.service.dto.CommentGetDto;
import prgrms.project.stuti.domain.feed.service.dto.CommentParentContents;
import prgrms.project.stuti.domain.feed.service.dto.CommentResponse;
import prgrms.project.stuti.domain.feed.service.dto.CommentUpdateDto;
import prgrms.project.stuti.global.page.offset.PageResponse;

@RestController
@RequiredArgsConstructor
public class CommentController {

	private final CommentService commentService;

	@PostMapping("/api/v1/posts/{postId}/comments")
	public ResponseEntity<CommentResponse> createComment(@PathVariable Long postId,
		@Valid @RequestBody CommentRequest commentRequest, @AuthenticationPrincipal Long memberId) {
		CommentCreateDto commentCreateDto = CommentMapper.toCommentCreateDto(commentRequest, postId, memberId);
		CommentResponse commentResponse = commentService.createComment(commentCreateDto);
		URI uri = URI.create("/api/v1/posts/" + postId + "/comments/" + commentResponse.postCommentId());

		return ResponseEntity.created(uri).body(commentResponse);
	}

	@PatchMapping("/api/v1/posts/{postId}/comments/{commentId}")
	public ResponseEntity<CommentResponse> changeComment(@PathVariable Long postId, @PathVariable Long commentId,
		@Valid @RequestBody CommentRequest commentRequest, @AuthenticationPrincipal Long memberId) {
		CommentUpdateDto commentUpdateDto = CommentMapper.toCommentUpdateDto(commentRequest, postId, commentId, memberId);
		CommentResponse commentResponse = commentService.changeComment(commentUpdateDto);

		return ResponseEntity.ok().body(commentResponse);
	}

	@DeleteMapping("/api/v1/posts/{postId}/comments/{commentId}")
	public ResponseEntity<Void> deleteComment(@PathVariable Long postId, @PathVariable Long commentId,
		@AuthenticationPrincipal Long memberId) {
		commentService.deleteComment(postId, commentId, memberId);
		final HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);

		return ResponseEntity.noContent().headers(httpHeaders).build();
	}

	@GetMapping("/api/v1/posts/{postId}/comments")
	public ResponseEntity<PageResponse<CommentParentContents>> getPostComments(@PathVariable Long postId,
		@RequestParam(value = "lastPostId", required = false) Long lastPostId,
		@RequestParam(defaultValue = "10") int size) {
		CommentGetDto commentGetDto = CommentConverter.toCommentGetDto(postId, lastPostId, size);
		PageResponse<CommentParentContents> commentResponse = commentService.getPostComments(commentGetDto);

		return ResponseEntity.ok().body(commentResponse);
	}
}
