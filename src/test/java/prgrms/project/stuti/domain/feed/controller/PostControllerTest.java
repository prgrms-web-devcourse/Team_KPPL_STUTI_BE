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
import org.springframework.security.core.parameters.P;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;

import prgrms.project.stuti.config.TestConfig;
import prgrms.project.stuti.domain.feed.model.Post;
import prgrms.project.stuti.domain.feed.model.PostImage;
import prgrms.project.stuti.domain.feed.service.PostConverter;
import prgrms.project.stuti.domain.feed.service.PostService;
import prgrms.project.stuti.domain.feed.service.dto.PostResponse;
import prgrms.project.stuti.domain.feed.service.dto.PostIdResponse;
import prgrms.project.stuti.domain.feed.service.dto.PostListResponse;
import prgrms.project.stuti.domain.member.model.Career;
import prgrms.project.stuti.domain.member.model.Field;
import prgrms.project.stuti.domain.member.model.Mbti;
import prgrms.project.stuti.domain.member.model.Member;
import prgrms.project.stuti.domain.member.model.MemberRole;

@WebMvcTest(PostController.class)
class PostControllerTest extends TestConfig {

	@MockBean
	private PostService postService;

	@Test
	@DisplayName("게시글을 등록한다 - 이미지 제외")
	@WithMockUser(username = "1", roles = {"ADMIN", "MEMBER"})
	void TestRegisterPost() throws Exception {
		MockMultipartFile file = new MockMultipartFile("mockImage", "mockImage.jpg",
			MediaType.TEXT_PLAIN_VALUE, "mockImage.jpg".getBytes());
		Member member = new Member("test@gmail.com", "testNickname", Field.BACKEND, Career.JUNIOR,
			"imageUrl.com", "testGithub.com", Mbti.ENFJ, "testBlog.com", MemberRole.ROLE_MEMBER);
		Post post = new Post("게시글 내용", member);
		PostImage postImage = new PostImage("testPostUrl.img", post);

		when(postService.registerPost(any())).thenReturn(
			PostConverter.toPostResponse(post, member, postImage, 0L, List.of()));

		mockMvc.perform(
				multipart("/api/v1/posts")
					.file(file)
					.param("contents", "게시글")
					.with(SecurityMockMvcRequestPostProcessors.csrf()))
			.andExpect(status().isOk())
			.andDo(print());
	}

	@Test
	@DisplayName("전체 페이지를 조회한다")
	void testGetAllPosts() throws Exception {
		List<PostResponse> posts = new ArrayList<>();
		PostResponse postResponse = PostResponse.builder()
			.postId(1L)
			.memberId(1L)
			.nickname("testUser")
			.mbti(Mbti.ENFJ)
			.profileImageUrl("testProfileImage.jpg")
			.contents("테스트게시글")
			.postImageUrl("testPost.jpg")
			.updatedAt(LocalDateTime.now())
			.totalPostComments(1L)
			.likedMembers(List.of(1L, 2L))
			.build();
		posts.add(postResponse);
		PostListResponse postsResponse = new PostListResponse(posts, true);

		when(postService.getAllPosts(any(), anyInt())).thenReturn(postsResponse);

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
		Member member = new Member("test@gmail.com", "testNickname", Field.BACKEND, Career.JUNIOR,
			"imageUrl.com", "testGithub.com", Mbti.ENFJ, "testBlog.com", MemberRole.ROLE_MEMBER);
		Post post = new Post("게시글 내용", member);
		PostImage postImage = new PostImage("testPostUrl.img", post);

		when(postService.changePost(any())).thenReturn(
			PostConverter.toPostResponse(post, member, postImage, 3L, List.of(1L, 2L, 3L)));

		mockMvc.perform(
				multipart(HttpMethod.POST, "/api/v1/posts/{postId}", 1L)
					.file(file)
					.param("contents", "수정게시글입니다."))
			.andExpect(status().isOk())
			.andDo(print());
	}

	@Test
	@DisplayName("게시글을 삭제한다")
	void TestDeletePost() throws Exception {
		doNothing().when(postService).deletePost(any());

		mockMvc.perform(delete("/api/v1/posts/{postId}", 1L))
			.andExpect(status().isOk())
			.andDo(print());
	}

	@Test
	@DisplayName("내 게시글을 조회한다")
	@WithMockUser(username = "1", roles = {"ADMIN", "MEMBER"})
	void testGetMyPosts() throws Exception {
		List<PostResponse> posts = new ArrayList<>();
		PostResponse postResponse = PostResponse.builder()
			.postId(1L)
			.memberId(1L)
			.nickname("testUser")
			.mbti(Mbti.ENFJ)
			.profileImageUrl("testProfileImage.jpg")
			.contents("테스트게시글")
			.postImageUrl("testPost.jpg")
			.updatedAt(LocalDateTime.now())
			.totalPostComments(1L)
			.likedMembers(List.of(1L, 2L))
			.build();
		posts.add(postResponse);
		PostListResponse postsResponse = new PostListResponse(posts, true);

		when(postService.getMyPosts(any(), any(), anyInt())).thenReturn(postsResponse);

		mockMvc.perform(get("/api/v1/posts/members/{memberId}", 1L))
			.andExpect(status().isOk())
			.andDo(print());
	}

}
