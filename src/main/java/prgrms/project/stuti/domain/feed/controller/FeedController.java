package prgrms.project.stuti.domain.feed.controller;

import java.net.URI;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import prgrms.project.stuti.domain.feed.service.dto.PostDto;
import prgrms.project.stuti.domain.feed.controller.dto.RegisterPostRequest;
import prgrms.project.stuti.domain.feed.service.FeedService;

@RestController
@RequiredArgsConstructor
public class FeedController {

	private final FeedService feedService;

	@PostMapping("/api/v1/posts")
	public ResponseEntity<Long> registerPost(@Valid RegisterPostRequest registerPostRequest,
		@AuthenticationPrincipal User authentication) {
		Long memberId = Long.parseLong(authentication.getUsername());
		PostDto postDto = FeedMapper.toPostDto(registerPostRequest, memberId);
		Long postId = feedService.registerPost(postDto);

		return ResponseEntity.created(
			URI.create("/api/v1/post/" + postId)
		).body(postId);
	}
}
