package prgrms.project.stuti.domain.feed.controller;

import static org.mockito.Mockito.*;
import static org.springframework.http.HttpHeaders.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
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
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.ResultActions;

import prgrms.project.stuti.config.TestConfig;
import prgrms.project.stuti.domain.feed.service.PostService;
import prgrms.project.stuti.domain.feed.service.response.PostListResponse;
import prgrms.project.stuti.domain.feed.service.response.PostResponse;
import prgrms.project.stuti.domain.member.model.Mbti;

@WebMvcTest(PostController.class)
class PostControllerTest extends TestConfig {

	@MockBean
	private PostService postService;

	@Test
	@DisplayName("게시글을 등록한다")
	@WithMockUser(username = "1", roles = {"ADMIN", "MEMBER"})
	void createPost() throws Exception {
		MockMultipartFile file = new MockMultipartFile("mockImage", "mockImage.jpg",
			MediaType.TEXT_PLAIN_VALUE, "mockImage.jpg".getBytes());
		PostResponse postResponse = PostResponse.builder()
			.postId(1L)
			.memberId(1L)
			.nickname("테스트닉네임")
			.mbti(Mbti.ENFJ)
			.profileImageUrl("testUrl")
			.contents("테스트게시글 내용")
			.updatedAt(LocalDateTime.now())
			.postImageUrl("testUrl.com")
			.totalPostComments(30L)
			.likedMembers(List.of(1L))
			.build();

		when(postService.registerPost(any())).thenReturn(postResponse);

		ResultActions resultActions = mockMvc.perform(
				multipart("/api/v1/posts")
					.file("postImage", file.getBytes())
					.param("contents", "게시글")
			)
			.andDo(print());

		resultActions.andExpectAll(
			status().isOk(),
			content().json(objectMapper.writeValueAsString(postResponse))
		).andDo(
			document(
				COMMON_DOCS_NAME,
				requestHeaders(
					headerWithName(CONTENT_TYPE).description("컨텐츠 타입"),
					headerWithName(HOST).description("호스트")
				),
				requestParts(
					partWithName("postImage").description("게시글 첨부 이미지")
				),
				requestParameters(
					parameterWithName("contents").description("게시글 내용")
				),
				responseFields(
					fieldWithPath("postId").type(NUMBER).description("게시글 아이디"),
					fieldWithPath("memberId").type(NUMBER).description("작성자 아이디"),
					fieldWithPath("nickname").type(STRING).description("작성자 닉네임"),
					fieldWithPath("mbti").type(STRING).description("작성자 mbti"),
					fieldWithPath("profileImageUrl").type(STRING).description("작성자 프로필 이미지"),
					fieldWithPath("contents").type(STRING).description("게시글 내용"),
					fieldWithPath("postImageUrl").type(STRING).description("게시글 첨부 이미지"),
					fieldWithPath("updatedAt").type(DATE).description("수정일자"),
					fieldWithPath("totalPostComments").type(NUMBER).description("총 댓글개수"),
					fieldWithPath("likedMembers[]").type(ARRAY).description("좋아요 사람 id 리스트")
				)
			)
		);
	}

	@Test
	@DisplayName("전체 페이지를 조회한다")
	void getPosts() throws Exception {
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

		ResultActions resultActions = mockMvc.perform(get("/api/v1/posts")
				.characterEncoding(StandardCharsets.UTF_8)
				.param("size", "1")
				.contentType(MediaType.APPLICATION_JSON))
			.andDo(print());

		resultActions.andExpectAll(
			status().isOk(),
			content().contentType(MediaType.APPLICATION_JSON)
		).andDo(
			document(COMMON_DOCS_NAME,
				requestHeaders(
					headerWithName(CONTENT_TYPE).description("컨텐츠 타입"),
					headerWithName(HOST).description("호스트")
				),
				requestParameters(
					parameterWithName("lastPostId").description("마지막 조회 게시글 아이디").optional(),
					parameterWithName("size").description("조회 사이즈")
				),
				responseFields(
					fieldWithPath("posts[]").type(ARRAY).description("게시글 리스트"),
					fieldWithPath("posts[].postId").type(NUMBER).description("게시글 아이디"),
					fieldWithPath("posts[].memberId").type(NUMBER).description("작성자 아이디"),
					fieldWithPath("posts[].nickname").type(STRING).description("작성자 닉네임"),
					fieldWithPath("posts[].mbti").type(STRING).description("작성자 mbti"),
					fieldWithPath("posts[].profileImageUrl").type(STRING).description("작성자 프로필 이미지"),
					fieldWithPath("posts[].contents").type(STRING).description("게시글 내용"),
					fieldWithPath("posts[].postImageUrl").type(STRING).description("게시글 첨부 이미지"),
					fieldWithPath("posts[].updatedAt").type(DATE).description("수정일자"),
					fieldWithPath("posts[].totalPostComments").type(NUMBER).description("총 댓글개수"),
					fieldWithPath("posts[].likedMembers[]").type(ARRAY).description("좋아요 사람 id 리스트"),
					fieldWithPath("hasNext").type(BOOLEAN).description("다음 게시글 존재여부")
				)
			)
		);
	}

	@Test
	@DisplayName("게시글을 수정한다")
	void updatePost() throws Exception {
		MockMultipartFile file = new MockMultipartFile("mockImage", "mockImage.jpg",
			MediaType.TEXT_PLAIN_VALUE, "mockImage.jpg".getBytes());
		PostResponse postResponse = PostResponse.builder()
			.postId(1L)
			.memberId(1L)
			.nickname("테스트닉네임")
			.mbti(Mbti.ENFJ)
			.profileImageUrl("testUrl")
			.contents("테스트게시글 내용")
			.updatedAt(LocalDateTime.now())
			.postImageUrl("testUrl.com")
			.totalPostComments(30L)
			.likedMembers(List.of(1L))
			.build();

		when(postService.changePost(any())).thenReturn(postResponse);

		ResultActions resultActions = mockMvc.perform(
				multipart("/api/v1/posts/{postId}", 1L)
					.file("postImage", file.getBytes())
					.param("contents", "수정게시글입니다."))
			.andDo(print());

		resultActions.andExpectAll(
			status().isOk()
		).andDo(
			document(
				COMMON_DOCS_NAME,
				requestHeaders(
					headerWithName(CONTENT_TYPE).description("컨텐츠 타입"),
					headerWithName(HOST).description("호스트")
				),
				requestParts(
					partWithName("postImage").description("게시글 첨부 이미지")
				),
				requestParameters(
					parameterWithName("contents").description("게시글 내용")
				),
				responseFields(
					fieldWithPath("postId").type(NUMBER).description("게시글 아이디"),
					fieldWithPath("memberId").type(NUMBER).description("작성자 아이디"),
					fieldWithPath("nickname").type(STRING).description("작성자 닉네임"),
					fieldWithPath("mbti").type(STRING).description("작성자 mbti"),
					fieldWithPath("profileImageUrl").type(STRING).description("작성자 프로필 이미지"),
					fieldWithPath("contents").type(STRING).description("게시글 내용"),
					fieldWithPath("postImageUrl").type(STRING).description("게시글 첨부 이미지"),
					fieldWithPath("updatedAt").type(DATE).description("수정일자"),
					fieldWithPath("totalPostComments").type(NUMBER).description("총 댓글개수"),
					fieldWithPath("likedMembers[]").type(ARRAY).description("좋아요 사람 id 리스트")
				)
			)
		);
	}

	@Test
	@DisplayName("게시글을 삭제한다")
	void deletePost() throws Exception {
		doNothing().when(postService).deletePost(any());

		ResultActions resultActions = mockMvc.perform(delete("/api/v1/posts/{postId}", 1L)
				.contentType(MediaType.APPLICATION_JSON))
			.andDo(print());

		resultActions.andExpectAll(
			status().isOk()
		).andDo(
			document(COMMON_DOCS_NAME,
				requestHeaders(
					headerWithName(CONTENT_TYPE).description("컨텐츠 타입"),
					headerWithName(HOST).description("호스트")
				),
				pathParameters(
					parameterWithName("postId").description("게시글 아이디")
				)
			)
		);
	}

	@Test
	@DisplayName("내 게시글을 조회한다")
	@WithMockUser(username = "1", roles = {"ADMIN", "MEMBER"})
	void getMembersPosts() throws Exception {
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

		when(postService.getMemberPosts(any(), any(), anyInt())).thenReturn(postsResponse);

		ResultActions resultActions = mockMvc.perform(get("/api/v1/posts/members/{memberId}", 1L)
				.contentType(MediaType.APPLICATION_JSON)
				.param("lastPostId", "3")
				.param("size", "3"))
			.andDo(print());

		resultActions.andExpectAll(
			status().isOk(),
			content().contentType(MediaType.APPLICATION_JSON)
		).andDo(
			document(COMMON_DOCS_NAME,
				requestHeaders(
					headerWithName(CONTENT_TYPE).description("컨텐츠 타입"),
					headerWithName(HOST).description("호스트")
				),
				pathParameters(
					parameterWithName("memberId").description("조회 대상 멤버 아이디")
				),
				requestParameters(
					parameterWithName("lastPostId").description("마지막 조회 게시글 아이디"),
					parameterWithName("size").description("조회 사이즈")
				),
				responseHeaders(

				),
				responseFields(
					fieldWithPath("posts[]").type(ARRAY).description("게시글 리스트"),
					fieldWithPath("posts[].postId").type(NUMBER).description("게시글 아이디"),
					fieldWithPath("posts[].memberId").type(NUMBER).description("작성자 아이디"),
					fieldWithPath("posts[].nickname").type(STRING).description("작성자 닉네임"),
					fieldWithPath("posts[].mbti").type(STRING).description("작성자 mbti"),
					fieldWithPath("posts[].profileImageUrl").type(STRING).description("작성자 프로필 이미지"),
					fieldWithPath("posts[].contents").type(STRING).description("게시글 내용"),
					fieldWithPath("posts[].postImageUrl").type(STRING).description("게시글 첨부 이미지"),
					fieldWithPath("posts[].updatedAt").type(DATE).description("수정일자"),
					fieldWithPath("posts[].totalPostComments").type(NUMBER).description("총 댓글개수"),
					fieldWithPath("posts[].likedMembers[]").type(ARRAY).description("좋아요 사람 id 리스트"),
					fieldWithPath("hasNext").type(BOOLEAN).description("다음 게시글 존재여부")
				)
			)
		);
	}

}
