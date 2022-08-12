package prgrms.project.stuti.domain.feed.controller;

import javax.validation.Valid;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import prgrms.project.stuti.domain.feed.controller.dto.PostRequest;
import prgrms.project.stuti.domain.feed.service.PostService;
import prgrms.project.stuti.domain.feed.service.dto.PostChangeDto;
import prgrms.project.stuti.domain.feed.service.dto.PostCreateDto;
import prgrms.project.stuti.domain.feed.service.response.PostListResponse;
import prgrms.project.stuti.domain.feed.service.response.PostResponse;

@RestController
@RequiredArgsConstructor
public class PostController {

	private final PostService postService;

	@PostMapping("/api/v1/posts")
	public ResponseEntity<PostResponse> registerPost(
		@Valid @ModelAttribute PostRequest registerPostRequest, @AuthenticationPrincipal Long memberId
	) {
		PostCreateDto postCreateDto = PostMapper.toPostCreateDto(registerPostRequest, memberId);
		PostResponse postResponse = postService.registerPost(postCreateDto);

		return ResponseEntity.ok(postResponse);
	}

	@GetMapping("/api/v1/posts")
	public ResponseEntity<PostListResponse> getAllPosts(
		@RequestParam(value = "lastPostId", required = false) Long lastPostId,
		@RequestParam(defaultValue = "10") int size
	) {
		PostListResponse postListResponse = postService.getAllPosts(lastPostId, size);

		return ResponseEntity.ok().body(postListResponse);
	}

	@PostMapping("/api/v1/posts/{postId}")
	public ResponseEntity<PostResponse> changePost(
		@PathVariable Long postId, @Valid @ModelAttribute PostRequest registerPostRequest
	) {
		PostChangeDto postChangeDto = PostMapper.toPostChangeDto(registerPostRequest, postId);
		PostResponse postResponse = postService.changePost(postChangeDto);

		return ResponseEntity.ok().body(postResponse);
	}

	@DeleteMapping("/api/v1/posts/{postId}")
	public ResponseEntity<Void> deletePost(
		@PathVariable Long postId
	) {
		postService.deletePost(postId);

		return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).build();
	}

	@GetMapping("/api/v1/posts/members/{memberId}")
	public ResponseEntity<PostListResponse> getMemberPosts(
		@PathVariable Long memberId, @RequestParam(value = "lastPostId", required = false) Long lastPostId,
		@RequestParam(defaultValue = "10") int size
	) {
		PostListResponse postListResponse = postService.getMemberPosts(memberId, lastPostId, size);

		return ResponseEntity.ok().body(postListResponse);
	}
}
