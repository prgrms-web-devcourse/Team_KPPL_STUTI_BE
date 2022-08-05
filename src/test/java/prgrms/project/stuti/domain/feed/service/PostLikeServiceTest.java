package prgrms.project.stuti.domain.feed.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import prgrms.project.stuti.config.ServiceTestConfig;
import prgrms.project.stuti.domain.feed.model.Feed;
import prgrms.project.stuti.domain.feed.model.FeedLike;
import prgrms.project.stuti.domain.feed.repository.FeedRepository;
import prgrms.project.stuti.domain.feed.repository.PostLikeRepository;
import prgrms.project.stuti.domain.feed.service.dto.PostLikeIdResponse;
import prgrms.project.stuti.domain.member.model.Member;
import prgrms.project.stuti.global.error.exception.FeedException;

@SpringBootTest
class PostLikeServiceTest extends ServiceTestConfig {

	@Autowired
	private PostLikeService postLikeService;

	@Autowired
	private FeedRepository feedRepository;

	@Autowired
	private PostLikeRepository postLikeRepository;

	@Test
	@DisplayName("좋아요를 등록한다.")
	void testCreatePostLike() {
		Feed post = createPost(member);

		PostLikeIdResponse postLike = postLikeService.createPostLike(post.getId(), member.getId());
		Optional<FeedLike> foundLike = postLikeRepository.findById(postLike.postLikeId());

		assertThat(foundLike).isNotEmpty();
		assertThat(foundLike.get().getId()).isEqualTo(postLike.postLikeId());
	}

	@Test
	@DisplayName("존재하지 않는 게시글에는 좋아요를 등록할 수 없다.")
	void testCreatePostLikeWithUnknownPost() {
		Long memberId = member.getId();

		assertThrows(FeedException.class, () -> postLikeService.createPostLike(0L, memberId));
	}

	@Test
	@DisplayName("중복으로 좋아요를 등록할 수 없다.")
	void testCreatePostDuplicateException() {
		Feed post = createPost(member);

		postLikeService.createPostLike(post.getId(), member.getId());

		assertThrows(FeedException.class, () ->postLikeService.createPostLike(post.getId(), member.getId()));
	}

	private Feed createPost(Member member) {
		Feed feed = new Feed("테스트 게시글 1번", member);
		return feedRepository.save(feed);
	}

}