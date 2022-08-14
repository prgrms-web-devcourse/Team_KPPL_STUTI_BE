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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.ResultActions;

import prgrms.project.stuti.config.TestConfig;
import prgrms.project.stuti.domain.feed.controller.dto.PostCommentRequest;
import prgrms.project.stuti.domain.feed.service.PostCommentService;
import prgrms.project.stuti.domain.feed.service.dto.PostCommentChild;
import prgrms.project.stuti.domain.feed.service.dto.PostCommentParent;
import prgrms.project.stuti.domain.feed.service.response.PostCommentContentsResponse;
import prgrms.project.stuti.domain.feed.service.response.PostCommentResponse;
import prgrms.project.stuti.global.page.PageResponse;

@WebMvcTest(PostCommentController.class)
class PostCommentControllerTest extends TestConfig {

	@MockBean
	private PostCommentService postCommentService;

	@Test
	@WithMockUser(username = "1", roles = {"ADMIN", "MEMBER"})
	@DisplayName("댓글을 등록한다")
	void createComment() throws Exception {
		PostCommentResponse postCommentResponse = PostCommentResponse.builder()
			.postCommentId(1L)
			.parentId(null)
			.profileImageUrl("www.test.prgrm/image.jpg")
			.memberId(1L)
			.nickname("testNickname")
			.contents("새로운 댓글입니다.")
			.updatedAt(LocalDateTime.now())
			.build();
		String requestBody =
			objectMapper.writeValueAsString(new PostCommentRequest(null, "테스트 댓글을 답니다."));

		when(postCommentService.createComment(any())).thenReturn(postCommentResponse);

		ResultActions resultActions = mockMvc.perform(post("/api/v1/posts/{postId}/comments", 1L)
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody)
				.characterEncoding(StandardCharsets.UTF_8))
			.andDo(print());

		resultActions
			.andExpectAll(
				status().isOk(),
				content().contentType(MediaType.APPLICATION_JSON),
				content().json(objectMapper.writeValueAsString(postCommentResponse))
			).andDo(
				document(COMMON_DOCS_NAME,
					requestHeaders(
						headerWithName(CONTENT_TYPE).description("컨텐츠 타입"),
						headerWithName(HOST).description("호스트")),
					pathParameters(parameterWithName("postId").description("게시글 아이디")),
					requestFields(
						fieldWithPath("parentId").type(VARIES).description("부모댓글 아이디"),
						fieldWithPath("contents").type(STRING).description("댓글 내용")
					),
					responseFields(
						fieldWithPath("postCommentId").type(NUMBER).description("댓글 아이디"),
						fieldWithPath("parentId").type(VARIES).description("부모댓글 아이디"),
						fieldWithPath("profileImageUrl").type(STRING).description("작성자 프로필 이미지"),
						fieldWithPath("memberId").type(NUMBER).description("작성자 멤버 아이디"),
						fieldWithPath("nickname").type(STRING).description("작성자 닉네임"),
						fieldWithPath("contents").type(STRING).description("댓글 내용"),
						fieldWithPath("updatedAt").type(DATE).description("작성, 수정일자")
					)
				)
			);
	}

	@Test
	@WithMockUser(username = "1", roles = {"ADMIN", "MEMBER"})
	@DisplayName("댓글을 수정한다.")
	void updateComment() throws Exception {
		PostCommentResponse postCommentResponse = PostCommentResponse.builder()
			.postCommentId(1L)
			.parentId(null)
			.profileImageUrl("www.test.prgrm/image.jpg")
			.memberId(1L)
			.nickname("testNickname")
			.contents("새로운 댓글입니다.")
			.updatedAt(LocalDateTime.now())
			.build();

		String requestBody =
			objectMapper.writeValueAsString(new PostCommentRequest(null, "댓글을 수정합니다."));

		when(postCommentService.changeComment(any())).thenReturn(postCommentResponse);

		ResultActions resultActions = mockMvc.perform(post("/api/v1/posts/{postId}/comments/{commentId}", 1L, 3L)
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
			.andDo(print());

		resultActions
			.andExpectAll(
				status().isOk(),
				content().contentType(MediaType.APPLICATION_JSON),
				content().json(objectMapper.writeValueAsString(postCommentResponse))
			).andDo(
				document(COMMON_DOCS_NAME,
					requestHeaders(
						headerWithName(CONTENT_TYPE).description("컨텐츠 타입"),
						headerWithName(HOST).description("호스트")),
					pathParameters(
						parameterWithName("postId").description("게시글 아이디"),
						parameterWithName("commentId").description("댓글 아이디")
					),
					requestFields(
						fieldWithPath("parentId").type(VARIES).description("부모댓글 아이디").optional(),
						fieldWithPath("contents").type(STRING).description("수정할 댓글 내용")
					),
					responseFields(
						fieldWithPath("postCommentId").type(NUMBER).description("댓글 아이디"),
						fieldWithPath("parentId").type(VARIES).description("부모댓글 아이디"),
						fieldWithPath("profileImageUrl").type(STRING).description("작성자 프로필 이미지"),
						fieldWithPath("memberId").type(NUMBER).description("작성자 멤버 아이디"),
						fieldWithPath("nickname").type(STRING).description("작성자 닉네임"),
						fieldWithPath("contents").type(STRING).description("댓글 내용"),
						fieldWithPath("updatedAt").type(DATE).description("작성, 수정일자")
					))
			);
	}

	@Test
	@WithMockUser(username = "1", roles = {"ADMIN", "MEMBER"})
	@DisplayName("댓글을 삭제한다")
	void deleteComment() throws Exception {
		PostCommentResponse postCommentResponse = PostCommentResponse.builder()
			.postCommentId(1L)
			.parentId(null)
			.profileImageUrl("www.test.prgrm/image.jpg")
			.memberId(1L)
			.nickname("testNickname")
			.contents("새로운 댓글입니다.")
			.updatedAt(LocalDateTime.now())
			.build();

		when(postCommentService.deleteComment(anyLong(), anyLong(), anyLong()))
			.thenReturn(postCommentResponse);

		ResultActions resultActions = mockMvc.perform(delete("/api/v1/posts/{postId}/comments/{commentId}", "1", "3")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andDo(print());

		resultActions
			.andExpectAll(
				status().isOk(),
				content().contentType(MediaType.APPLICATION_JSON),
				content().json(objectMapper.writeValueAsString(postCommentResponse))
			).andDo(
				document(
					COMMON_DOCS_NAME,
					requestHeaders(
						headerWithName(CONTENT_TYPE).description("컨텐츠 타입"),
						headerWithName(HOST).description("호스트")
					),
					pathParameters(
						parameterWithName("postId").description("게시글 아이디"),
						parameterWithName("commentId").description("댓글 ")
					),
					responseFields(
						fieldWithPath("postCommentId").type(NUMBER).description("댓글 아이디"),
						fieldWithPath("parentId").type(VARIES).description("부모댓글 아이디"),
						fieldWithPath("profileImageUrl").type(STRING).description("작성자 프로필 이미지"),
						fieldWithPath("memberId").type(NUMBER).description("작성자 멤버 아이디"),
						fieldWithPath("nickname").type(STRING).description("작성자 닉네임"),
						fieldWithPath("contents").type(STRING).description("댓글 내용"),
						fieldWithPath("updatedAt").type(DATE).description("작성, 수정일자")
					)
				)
			);
	}

	@Test
	@DisplayName("게시글의 댓글을 페이징하여 조회한다")
	void getComments() throws Exception {
		List<PostCommentParent> contents = new ArrayList<>();
		List<PostCommentChild> childContents = new ArrayList<>();
		PostCommentChild childContent = new PostCommentChild(
			1L, 2L, "testurl.com", 1L,
			"testnickname", "대댓글", LocalDateTime.now());
		childContents.add(childContent);
		PostCommentParent commentParentContents = new PostCommentParent(
			1L, null, "testurl.com", 1L, "testnickname",
			"댓글입니다.", LocalDateTime.now(), childContents);
		contents.add(commentParentContents);
		PageResponse<PostCommentParent> pageResponse = new PageResponse<>(contents, true, 3L);

		when(postCommentService.getPostComments(any())).thenReturn(pageResponse);

		ResultActions resultActions = mockMvc.perform(get("/api/v1/posts/{postId}/comments", 1L)
				.param("lastCommentId", "3")
				.param("size", "3")
				.contentType(MediaType.APPLICATION_JSON))
			.andDo(print());

		resultActions.andExpectAll(
			status().isOk(),
			content().contentType(MediaType.APPLICATION_JSON),
			content().json(objectMapper.writeValueAsString(pageResponse))
		).andDo(
			document(COMMON_DOCS_NAME,
				requestHeaders(
					headerWithName(CONTENT_TYPE).description("컨텐츠 타입"),
					headerWithName(HOST).description("호스트")
				),
				pathParameters(
					parameterWithName("postId").description("게시글 아이디")
				),
				requestParameters(
					parameterWithName("lastCommentId").description("마지막 조회한 댓글 아이디").optional(),
					parameterWithName("size").description("요청 댓글 개수")
				),
				responseFields(
					fieldWithPath("contents[]").type(ARRAY).description("댓글 배열"),
					fieldWithPath("contents[].postCommentId").type(NUMBER).description("댓글 아이디"),
					fieldWithPath("contents[].parentId").type(VARIES).description("부모 댓글 아이디"),
					fieldWithPath("contents[].profileImageUrl").type(STRING).description("작성자 프로필 이미지"),
					fieldWithPath("contents[].memberId").type(NUMBER).description("작성자 아이디"),
					fieldWithPath("contents[].nickname").type(STRING).description("작성자 닉네임"),
					fieldWithPath("contents[].contents").type(STRING).description("댓글 내용"),
					fieldWithPath("contents[].updatedAt").type(DATE).description("수정일자"),
					fieldWithPath("contents[].children[]").type(ARRAY).description("대댓글 배열"),
					fieldWithPath("contents[].children[].parentId").type(NUMBER).description("부모 댓글 아이디"),
					fieldWithPath("contents[].children[].postCommentId").type(NUMBER).description("대댓글 아이디"),
					fieldWithPath("contents[].children[].profileImageUrl").type(STRING).description("작성자 프로필 이미지"),
					fieldWithPath("contents[].children[].memberId").type(NUMBER).description("작성자 아이디"),
					fieldWithPath("contents[].children[].nickname").type(STRING).description("작성자 닉네임"),
					fieldWithPath("contents[].children[].contents").type(STRING).description("대댓글 내용"),
					fieldWithPath("contents[].children[].updatedAt").type(DATE).description("수정일자"),
					fieldWithPath("hasNext").type(BOOLEAN).description("다음 댓글 존재여부"),
					fieldWithPath("totalElements").type(NUMBER).description("총 댓글개수")
				)
			)
		);
	}

	@Test
	@DisplayName("댓글 id로 댓글의 내용을 반환한다")
	void GetComment() throws Exception {
		PostCommentContentsResponse postCommentContentsResponse = new PostCommentContentsResponse(1L, 1L, "테스트 댓글입니다.");

		when(postCommentService.getCommentContents(anyLong(), anyLong())).thenReturn(postCommentContentsResponse);

		ResultActions resultActions = mockMvc.perform(get("/api/v1/posts/{postId}/comments/{commentId}", 1L, 1L)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andDo(print());

		resultActions.andExpectAll(
			status().isOk(),
			content().json(objectMapper.writeValueAsString(postCommentContentsResponse))
		).andDo(
			document(COMMON_DOCS_NAME,
				requestHeaders(
					headerWithName(CONTENT_TYPE).description("컨텐츠 타입"),
					headerWithName(HOST).description("호스트")
				),
				pathParameters(
					parameterWithName("postId").description("게시글 아이디"),
					parameterWithName("commentId").description("댓글 아이디")
				),
				responseFields(
					fieldWithPath("postCommentId").type(NUMBER).description("댓글 아이디"),
					fieldWithPath("parentId").type(NUMBER).description("부모 댓글 아이디"),
					fieldWithPath("contents").type(STRING).description("댓글 내용")
				)
			)
		);
	}

}