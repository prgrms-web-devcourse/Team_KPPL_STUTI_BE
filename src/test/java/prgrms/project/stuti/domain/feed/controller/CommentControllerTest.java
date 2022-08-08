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
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import prgrms.project.stuti.config.TestConfig;
import prgrms.project.stuti.domain.feed.controller.dto.CommentRequest;
import prgrms.project.stuti.domain.feed.service.CommentService;
import prgrms.project.stuti.domain.feed.service.dto.CommentContentsResponse;
import prgrms.project.stuti.domain.feed.service.dto.CommentParentContents;
import prgrms.project.stuti.domain.feed.service.dto.CommentResponse;
import prgrms.project.stuti.global.page.offset.PageResponse;

@WebMvcTest(CommentController.class)
class CommentControllerTest extends TestConfig {

	@MockBean
	private CommentService commentService;

	@Test
	@WithMockUser(username = "1", roles = {"ADMIN", "MEMBER"})
	@DisplayName("댓글을 등록한다")
	void testCreateComment() throws Exception {
		CommentResponse commentResponse = CommentResponse.builder()
			.postCommentId(1L)
			.parentId(null)
			.profileImageUrl("www.test.prgrm/image.jpg")
			.memberId(1L)
			.nickname("testNickname")
			.contents("새로운 댓글입니다.")
			.updatedAt(LocalDateTime.now())
			.build();
		String requestBody =
			objectMapper.writeValueAsString(new CommentRequest(null, "테스트 댓글을 답니다."));

		when(commentService.createComment(any())).thenReturn(commentResponse);

		mockMvc.perform(post("/api/v1/posts/{postId}/comments", 1L)
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody)
				.characterEncoding(StandardCharsets.UTF_8))
			.andExpect(status().isCreated())
			.andDo(print());
	}

	@Test
	@WithMockUser(username = "1", roles = {"ADMIN", "MEMBER"})
	@DisplayName("댓글을 수정한다.")
	void testChangeComment() throws Exception {
		CommentResponse commentResponse = CommentResponse.builder()
			.postCommentId(1L)
			.parentId(null)
			.profileImageUrl("www.test.prgrm/image.jpg")
			.memberId(1L)
			.nickname("testNickname")
			.contents("새로운 댓글입니다.")
			.updatedAt(LocalDateTime.now())
			.build();

		String requestBody =
			objectMapper.writeValueAsString(new CommentRequest(null, "댓글을 수정합니다."));

		when(commentService.changeComment(any())).thenReturn(commentResponse);

		mockMvc.perform(patch("/api/v1/posts/{postId}/comments/{commentId}", 1L, 3L)
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
			.andExpect(status().isOk())
			.andDo(print());
	}

	@Test
	@WithMockUser(username = "1", roles = {"ADMIN", "MEMBER"})
	@DisplayName("댓글을 삭제한다")
	void testDeleteComment() throws Exception {
		doNothing().when(commentService).deleteComment(anyLong(), anyLong(), anyLong());

		mockMvc.perform(delete("/api/v1/posts/{postId}/comments/{commentId}", 1L, 3L))
			.andExpect(status().isNoContent())
			.andDo(print());
	}

	@Test
	@DisplayName("게시글의 댓글을 페이징하여 조회한다")
	void testGetAllCommentsByPostId() throws Exception {
		List<CommentParentContents> contents = new ArrayList<>();
		PageResponse<CommentParentContents> pageResponse = new PageResponse<>(contents, true, 3L);

		when(commentService.getPostComments(any())).thenReturn(pageResponse);

		mockMvc.perform(get("/api/v1/posts/{postId}/comments", 1L)
				.param("lastPostId", "3")
				.param("size", "3"))
			.andExpect(status().isOk())
			.andDo(print());
	}

	@Test
	@DisplayName("댓글 id로 댓글의 내용을 반환한다")
	void testGetCommentContents() throws Exception {
		CommentContentsResponse commentContentsResponse = new CommentContentsResponse(1L, 1L, "테스트 댓글입니다.");

		when(commentService.getCommentContents(anyLong(), anyLong())).thenReturn(commentContentsResponse);

		mockMvc.perform(get("/api/v1/posts/{postId}/comments/{commentId}", 1L, 1L))
			.andExpect(status().isOk())
			.andDo(print());
	}

}