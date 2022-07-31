package prgrms.project.stuti.domain.feed.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import prgrms.project.stuti.domain.feed.controller.dto.RegisterPostRequest;
import prgrms.project.stuti.domain.feed.service.FeedService;

@WebMvcTest
class FeedControllerTest {

	@MockBean
	private FeedService feedService;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	protected ObjectMapper objectMapper;

	@Test
	@DisplayName("게시글을 등록한다 - 이미지 제외")
	@WithMockUser(username = "1", roles = {"ADMIN", "USER"})
	void TestRegisterPost() throws Exception {

		String content = objectMapper.writeValueAsString(
			new RegisterPostRequest(List.of(), "게시글 생성"));

		when(feedService.registerPost(any())).thenReturn(1L);

		mockMvc.perform(post("/api/v1/posts")
				.param("content", "게시글")
				.contentType(MediaType.APPLICATION_JSON)
				.with(SecurityMockMvcRequestPostProcessors.csrf()))
			.andExpect(status().isCreated())
			.andDo(print());

	}
}