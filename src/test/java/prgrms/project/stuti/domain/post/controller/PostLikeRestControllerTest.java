package prgrms.project.stuti.domain.post.controller;

import static org.mockito.BDDMockito.*;
import static org.springframework.http.HttpHeaders.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.ResultActions;

import prgrms.project.stuti.config.TestConfig;
import prgrms.project.stuti.domain.post.service.PostLikeService;
import prgrms.project.stuti.domain.post.service.response.PostLikeIdResponse;

@WebMvcTest(PostLikeRestController.class)
class PostLikeRestControllerTest extends TestConfig {

	@MockBean
	private PostLikeService postLikeService;

	@Test
	@WithMockUser(username = "1", roles = {"ADMIN", "MEMBER"})
	@DisplayName("게시글에 좋아요를 등록한다")
	void createPostLike() throws Exception {
		PostLikeIdResponse postLikeIdResponse = new PostLikeIdResponse(1L);

		when(postLikeService.createPostLike(any(), any())).thenReturn(postLikeIdResponse);

		ResultActions resultActions = mockMvc.perform(post("/api/v1/posts/{postId}/likes", 1L)
				.contentType(MediaType.APPLICATION_JSON));

		resultActions.andExpectAll(
			status().isOk(),
			content().contentType(MediaType.APPLICATION_JSON),
			content().json(objectMapper.writeValueAsString(postLikeIdResponse))
		).andDo(
			document(COMMON_DOCS_NAME,
				requestHeaders(
					headerWithName(CONTENT_TYPE).description("컨텐츠 타입")
				),
				pathParameters(
					parameterWithName("postId").description("게시글 아이디")
				),
				responseFields(
					fieldWithPath("postLikeId").type(NUMBER).description("좋아요 아이디")
				))
		);
	}

	@Test
	@WithMockUser(username = "1", roles = {"ADMIN", "MEMBER"})
	@DisplayName("좋아요를 취소한다")
	void deletePostLike() throws Exception {
		doNothing().when(postLikeService).cancelPostLike(anyLong(), anyLong());

		ResultActions resultActions = mockMvc.perform(delete("/api/v1/posts/{postId}/likes", 1L)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andDo(print());

		resultActions.andExpectAll(
			status().isOk()
		).andDo(document(
				COMMON_DOCS_NAME,
				requestHeaders(
					headerWithName(CONTENT_TYPE).description("컨텐츠 타입"),
					headerWithName(HOST).description("호스트")),
				pathParameters(
					parameterWithName("postId").description("게시글 아이디")
				)
			)
		);
	}
}