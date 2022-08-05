package prgrms.project.stuti.domain.feed.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import prgrms.project.stuti.config.TestConfig;
import prgrms.project.stuti.domain.feed.service.PostLikeService;
import prgrms.project.stuti.domain.feed.service.dto.PostLikeIdResponse;

@WebMvcTest(PostLikeController.class)
class PostLikeControllerTest extends TestConfig {

	@MockBean
	private PostLikeService postLikeService;

	@Test
	@WithMockUser(username = "1", roles = {"ADMIN", "MEMBER"})
	@DisplayName("게시글에 좋아요를 등록한다")
	void testCreatePostLike() throws Exception {
		PostLikeIdResponse postLikeIdResponse = PostLikeIdResponse.builder()
			.postLikeId(1L).build();

		when(postLikeService.createPostLike(anyLong(), anyLong()))
			.thenReturn(postLikeIdResponse);

		mockMvc.perform(post("/api/v1/posts/{postId}/like", 1L)
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding(StandardCharsets.UTF_8))
			.andExpect(status().isOk())
			.andDo(print());
	}
}