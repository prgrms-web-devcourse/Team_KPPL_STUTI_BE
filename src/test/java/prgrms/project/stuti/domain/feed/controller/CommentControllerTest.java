package prgrms.project.stuti.domain.feed.controller;

import static org.junit.jupiter.api.Assertions.*;
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
import prgrms.project.stuti.domain.feed.controller.dto.CommentRequest;
import prgrms.project.stuti.domain.feed.service.CommentService;
import prgrms.project.stuti.domain.feed.service.dto.CommentIdResponse;

@WebMvcTest(CommentController.class)
class CommentControllerTest extends TestConfig {

	@MockBean
	private CommentService commentService;

	@Test
	@WithMockUser(username = "1", roles = {"ADMIN", "MEMBER"})
	@DisplayName("댓글을 등록한다")
	void testCreateComment() throws Exception {
		CommentIdResponse commentIdResponse = new CommentIdResponse(1L);
		String requestBody =
			objectMapper.writeValueAsString(new CommentRequest(null, "테스트 댓글을 답니다."));

		when(commentService.createComment(any())).thenReturn(commentIdResponse);

		mockMvc.perform(post("/api/v1/posts/{postId}/comments", 1L)
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody)
				.characterEncoding(StandardCharsets.UTF_8))
			.andExpect(status().isCreated())
			.andDo(print());
	}

}