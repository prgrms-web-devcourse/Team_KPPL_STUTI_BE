package prgrms.project.stuti.domain.feed.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;

import prgrms.project.stuti.config.TestConfig;
import prgrms.project.stuti.domain.feed.service.FeedConverter;
import prgrms.project.stuti.domain.feed.service.FeedService;
import prgrms.project.stuti.domain.feed.service.dto.PostDto;
import prgrms.project.stuti.domain.feed.service.dto.FeedResponse;
import prgrms.project.stuti.domain.feed.service.dto.PostIdResponse;
import prgrms.project.stuti.domain.member.model.Mbti;

@WebMvcTest(FeedController.class)
class FeedControllerTest extends TestConfig {

	@MockBean
	private FeedService feedService;

	@Test
	@DisplayName("게시글을 등록한다 - 이미지 제외")
	@WithMockUser(username = "1", roles = {"ADMIN", "MEMBER"})
	void TestRegisterPost() throws Exception {
		MockMultipartFile file = new MockMultipartFile("mockImage", "mockImage.jpg",
			MediaType.TEXT_PLAIN_VALUE, "mockImage.jpg".getBytes());

		when(feedService.registerPost(any())).thenReturn(FeedConverter.toPostIdResponse(1L));

		mockMvc.perform(
				multipart("/api/v1/posts")
					.file(file)
					.param("content", "게시글")
					.with(SecurityMockMvcRequestPostProcessors.csrf()))
			.andExpect(status().isCreated())
			.andDo(print());
	}

	@Test
	@DisplayName("전체 페이지를 조회한다")
	void testGetAllPosts() throws Exception {
		List<PostDto> posts = new ArrayList<>();
		PostDto postDto = PostDto.builder()
			.postId(1L)
			.memberId(1L)
			.nickname("testUser")
			.mbti(Mbti.ENFJ)
			.profileImageUrl("testProfileImage.jpg")
			.contents("테스트게시글")
			.postImageUrl("testPost.jpg")
			.createdAt(LocalDateTime.now())
			.totalComments(1)
			.totalLikes(1)
			.isliked(true)
			.build();
		posts.add(postDto);
		FeedResponse postsResponse = new FeedResponse(posts, true);

		when(feedService.getAllPosts(any(), anyInt())).thenReturn(postsResponse);

		mockMvc.perform(get("/api/v1/posts")
				.characterEncoding(StandardCharsets.UTF_8)
				.param("size", "1"))
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("게시글을 수정한다")
	void testChangePost() throws Exception {
		MockMultipartFile file = new MockMultipartFile("mockImage", "mockImage.jpg",
			MediaType.TEXT_PLAIN_VALUE, "mockImage.jpg".getBytes());

		when(feedService.changePost(any())).thenReturn(new PostIdResponse(1L));

		mockMvc.perform(
			multipart(HttpMethod.PATCH,"/api/v1/posts/{postId}", 1L)
				.file(file)
				.param("content", "수정게시글입니다."))
			.andExpect(status().isOk())
			.andDo(print());
	}

}