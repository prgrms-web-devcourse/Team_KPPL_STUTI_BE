package prgrms.project.stuti.domain.post.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import prgrms.project.stuti.domain.post.service.PostLikeService;
import prgrms.project.stuti.domain.post.service.response.PostLikeIdResponse;

@RestController
@RequiredArgsConstructor
public class PostLikeRestController {

	private final PostLikeService postLikeService;

	@PostMapping("/api/v1/posts/{postId}/likes")
	public ResponseEntity<PostLikeIdResponse> createPostLike(
		@PathVariable Long postId, @AuthenticationPrincipal Long memberId
	) {
		PostLikeIdResponse postLikeIdResponse = postLikeService.createPostLike(postId, memberId);

		return ResponseEntity.ok(postLikeIdResponse);
	}

	@DeleteMapping("/api/v1/posts/{postId}/likes")
	public ResponseEntity<Void> cancelPostLike(
		@PathVariable Long postId, @AuthenticationPrincipal Long memberId
	) {
		postLikeService.cancelPostLike(postId, memberId);

		return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).build();
	}
}
