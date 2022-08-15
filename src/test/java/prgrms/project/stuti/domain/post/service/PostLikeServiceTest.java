package prgrms.project.stuti.domain.post.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import prgrms.project.stuti.config.ServiceTestConfig;
import prgrms.project.stuti.domain.post.model.Post;
import prgrms.project.stuti.domain.post.model.PostLike;
import prgrms.project.stuti.domain.post.repository.PostLikeRepository;
import prgrms.project.stuti.domain.post.repository.post.PostRepository;
import prgrms.project.stuti.domain.post.service.response.PostLikeIdResponse;
import prgrms.project.stuti.domain.member.model.Member;
import prgrms.project.stuti.global.error.exception.PostException;

class PostLikeServiceTest extends ServiceTestConfig {

	@Autowired
	private PostLikeService postLikeService;

	@Autowired
	private PostRepository postRepository;

	@Autowired
	private PostLikeRepository postLikeRepository;

	@Test
	@DisplayName("좋아요를 등록한다.")
	void testCreatePostLike() {
		Post post = createPost(member);

		PostLikeIdResponse postLike = postLikeService.createPostLike(post.getId(), member.getId());
		Optional<PostLike> foundLike = postLikeRepository.findById(postLike.postLikeId());

		assertThat(foundLike).isNotEmpty();
		assertThat(foundLike.get().getId()).isEqualTo(postLike.postLikeId());
	}

	@Test
	@DisplayName("존재하지 않는 게시글에는 좋아요를 등록할 수 없다.")
	void testCreatePostLikeWithUnknownPost() {
		Long memberId = member.getId();

		assertThrows(PostException.class, () -> postLikeService.createPostLike(0L, memberId));
	}

	@Test
	@DisplayName("중복으로 좋아요를 등록할 수 없다.")
	void testCreatePostDuplicateException() {
		Post post = createPost(member);

		postLikeService.createPostLike(post.getId(), member.getId());

		assertThrows(PostException.class, () -> postLikeService.createPostLike(post.getId(), member.getId()));
	}

	@Test
	@DisplayName("좋아요를 취소한다.")
	void testCancelPostLike() {
		Post post = createPost(member);
		PostLikeIdResponse postLike = postLikeService.createPostLike(post.getId(), member.getId());

		postLikeService.cancelPostLike(post.getId(), member.getId());
		Optional<PostLike> foundPostLike = postLikeRepository.findById(postLike.postLikeId());

		assertThat(foundPostLike).isEmpty();
	}

	@Test
	@DisplayName("좋아요 하지 않은 게시글을 좋아요 취소 할 수 없다.")
	void testCancelPostLikeWithUnknownLike() {
		Post post = createPost(member);

		assertThrows(PostException.class, () -> postLikeService.cancelPostLike(post.getId(), member.getId()));
	}

	private Post createPost(Member member) {
		Post post = new Post("테스트 게시글 1번", member);
		return postRepository.save(post);
	}

}