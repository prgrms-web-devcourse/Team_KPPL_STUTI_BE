package prgrms.project.stuti.domain.feed.controller;

import java.net.URI;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
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
import prgrms.project.stuti.domain.feed.service.dto.CommentIdResponse;
import prgrms.project.stuti.global.page.offset.PageResponse;

@RestController
@RequiredArgsConstructor
public class CommentController {

	private final CommentService commentService;

	@PostMapping("/api/v1/posts/{postId}/comments")
	public ResponseEntity<CommentIdResponse> createComment(@PathVariable Long postId,
		@Valid @RequestBody CommentRequest commentRequest, @AuthenticationPrincipal Long memberId) {
		CommentCreateDto commentCreateDto = CommentMapper.toCommentCreateDto(commentRequest, postId, memberId);
		CommentIdResponse commentIdResponse = commentService.createComment(commentCreateDto);
		URI uri = URI.create("/api/v1/posts/" + postId + "/comments/" + commentIdResponse.commentId());

		return ResponseEntity.created(uri).body(commentIdResponse);
	}

	@GetMapping("/api/v1/posts/{postId}/comments")
	public ResponseEntity<PageResponse> getAllCommentsByPostId(@PathVariable Long postId,
		@RequestParam(value = "lastPostId", required = false) Long lastPostId,
		@RequestParam(defaultValue = "10") int size) {
		CommentGetDto commentGetDto = CommentConverter.toCommentGetDto(postId, lastPostId, size);
		PageResponse commentResponse = commentService.getAllCommentsByPostId(commentGetDto);

		return ResponseEntity.ok().body(commentResponse);
	}
}
