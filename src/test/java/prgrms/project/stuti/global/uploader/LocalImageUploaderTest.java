package prgrms.project.stuti.global.uploader;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static prgrms.project.stuti.global.uploader.common.ImageDirectory.*;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.UrlResource;
import org.springframework.mock.web.MockMultipartFile;

@ExtendWith(MockitoExtension.class)
class LocalImageUploaderTest {

	@InjectMocks
	private LocalImageUploader uploader;

	@Mock
	private ResourceLoader resourceLoader;

	private final String rootPath = "file:src/main/resources/static";

	@Test
	@DisplayName("이미지를 업로드한다.")
	void testUpload() throws IOException {
		//given
		given(resourceLoader.getResource(any())).willReturn(new UrlResource(rootPath));

		//when
		String imageUrl = uploader.upload(getMockImageFile(), STUDY_GROUP);
		uploader.delete(imageUrl);

		//then
		assertThat(imageUrl).isNotNull();
		assertThat(imageUrl.substring(imageUrl.lastIndexOf(".") + 1)).isEqualTo("PNG");
	}

	@Test
	@DisplayName("여러 이미지를 업로드한다.")
	void testUploadAll() throws IOException {
		//given
		given(resourceLoader.getResource(any())).willReturn(new UrlResource(rootPath));

		//when
		List<String> imageUrls =
			uploader.uploadAll(List.of(getMockImageFile(), getMockImageFile()), STUDY_GROUP);
		imageUrls.forEach(url -> uploader.delete(url));

		//then
		assertThat(imageUrls).hasSize(2);
		imageUrls.forEach(imageUrl -> assertThat(imageUrl).contains(STUDY_GROUP.getDirectory()));
	}

	private MockMultipartFile getMockImageFile() {
		return new MockMultipartFile(
			"test",
			"test.png",
			"image/png",
			"test".getBytes());
	}
}
