package prgrms.project.stuti.domain.feed.controller;

import javax.validation.Valid;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import prgrms.project.stuti.domain.feed.controller.dto.PostRequest;
import prgrms.project.stuti.domain.feed.service.PostService;
import prgrms.project.stuti.domain.feed.service.dto.PostChangeDto;
import prgrms.project.stuti.domain.feed.service.dto.PostCreateDto;
import prgrms.project.stuti.domain.feed.service.dto.PostIdResponse;
import prgrms.project.stuti.domain.feed.service.dto.PostResponse;

@RestController
@RequiredArgsConstructor
public class PostController {

	private final PostService postService;

	@PostMapping(path = "/api/v1/posts", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
	public ResponseEntity<PostIdResponse> registerPost(@Valid @ModelAttribute PostRequest registerPostRequest,
		@AuthenticationPrincipal Long memberId) {
		PostCreateDto postCreateDto = PostMapper.toPostCreateDto(registerPostRequest, memberId);
		PostIdResponse postIdResponse = postService.registerPost(postCreateDto);

		return ResponseEntity.ok(postIdResponse);
	}

	@GetMapping("/api/v1/posts")
	public ResponseEntity<PostResponse> getAllPosts(
		@RequestParam(value = "lastPostId", required = false) Long lastPostId,
		@RequestParam(defaultValue = "10") int size) {
		PostResponse postResponse = postService.getAllPosts(lastPostId, size);
		
		return ResponseEntity.ok().body(postResponse);
	}

	@PostMapping("/api/v1/posts/{postId}")
	public ResponseEntity<PostIdResponse> changePost(@Valid @ModelAttribute PostRequest registerPostRequest,
		@PathVariable Long postId) {
		PostChangeDto postChangeDto = PostMapper.toPostChangeDto(registerPostRequest, postId);
		PostIdResponse postIdResponse = postService.changePost(postChangeDto);

		return ResponseEntity.ok().body(postIdResponse);
	}

	@DeleteMapping("/api/v1/posts/{postId}")
	public ResponseEntity<Void> deletePost(@PathVariable Long postId) {
		postService.deletePost(postId);

		return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).build();
	}

	@GetMapping("/api/v1/posts/myposts")
	public ResponseEntity<PostResponse> getMyPosts(@AuthenticationPrincipal Long memberId,
		@RequestParam(value = "lastPostId", required = false) Long lastPostId,
		@RequestParam(defaultValue = "10") int size) {
		PostResponse postResponse = postService.getMyPosts(memberId, lastPostId, size);

		return ResponseEntity.ok().body(postResponse);
	}
}
