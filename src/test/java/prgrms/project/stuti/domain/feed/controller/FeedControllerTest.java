package prgrms.project.stuti.domain.feed.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.web.multipart.MultipartFile;

import prgrms.project.stuti.config.TestConfig;
import prgrms.project.stuti.domain.feed.service.FeedService;

@WebMvcTest(FeedController.class)
class FeedControllerTest extends TestConfig {

	@MockBean
	private FeedService feedService;

	@Test
	@DisplayName("게시글을 등록한다 - 이미지 제외")
	@WithMockUser(username = "1", roles = {"ADMIN", "USER"})
	void TestRegisterPost() throws Exception {

		MockMultipartFile file = new MockMultipartFile("mockImage", "mockImage.jpg",
			MediaType.TEXT_PLAIN_VALUE, "mockImage.jpg".getBytes());
		List<MultipartFile> files = new ArrayList<>();
		files.add(file);

		when(feedService.registerPost(any())).thenReturn(1L);

		mockMvc.perform(
			multipart("/api/v1/posts")
				.file(file)
				.param("content", "게시글")
				.with(SecurityMockMvcRequestPostProcessors.csrf()))
			.andExpect(status().isCreated())
			.andDo(print());
	}
}