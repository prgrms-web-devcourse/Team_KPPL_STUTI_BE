package prgrms.project.stuti.domain.feed.controller;

import java.net.URI;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import prgrms.project.stuti.domain.feed.controller.dto.CommentRequest;
import prgrms.project.stuti.domain.feed.service.dto.CommentUpdateDto;
import prgrms.project.stuti.domain.feed.service.CommentService;
import prgrms.project.stuti.domain.feed.service.dto.CommentCreateDto;
import prgrms.project.stuti.domain.feed.service.dto.CommentResponse;

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
}
