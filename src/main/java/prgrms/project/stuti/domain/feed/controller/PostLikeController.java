package prgrms.project.stuti.domain.feed.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import prgrms.project.stuti.domain.feed.service.PostLikeService;
import prgrms.project.stuti.domain.feed.service.dto.PostLikeIdResponse;

@RestController
@RequiredArgsConstructor
public class PostLikeController {

	private final PostLikeService postLikeService;

	@PostMapping("/api/v1/posts/{postId}/likes")
	public ResponseEntity<PostLikeIdResponse> createPostLike(@PathVariable Long postId,
		@AuthenticationPrincipal Long memberId) {
		PostLikeIdResponse postLikeIdResponse = postLikeService.createPostLike(postId, memberId);
		//URI uri = URI.create("/api/v1/post/" + postId + "/like/" + postLikeIdResponse.postLikeId());

		//return ResponseEntity.created(uri).body(postLikeIdResponse);
		return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(postLikeIdResponse);
	}

	@DeleteMapping("/api/v1/posts/{postId}/likes")
	public ResponseEntity<Void> cancelPostLike(@PathVariable Long postId, @AuthenticationPrincipal Long memberId) {
		postLikeService.cancelPostLike(postId, memberId);
		final HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);

		return ResponseEntity.noContent().headers(httpHeaders).build();
	}
}
