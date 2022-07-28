package prgrms.project.stuti.global.uploader;

import static org.assertj.core.api.Assertions.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import prgrms.project.stuti.global.error.exception.FileException;
import prgrms.project.stuti.global.uploader.common.ImageFileValidator;

class ImageFileValidatorTest {

	File emptyFile;
	File unsupportedContentTypeFile;

	@BeforeEach
	void setup() throws IOException {
		String testFilePath = Paths.get("src", "test", "resources").toString();
		emptyFile = new File(testFilePath + File.separator + "empty.png");
		unsupportedContentTypeFile = new File(testFilePath + File.separator + "unsupported.txt");

		assertThat(emptyFile.createNewFile()).isTrue();
		assertThat(unsupportedContentTypeFile.createNewFile()).isTrue();
	}

	@AfterEach
	void cleanup() throws IOException {
		Files.deleteIfExists(Path.of(emptyFile.getAbsolutePath()));
		Files.deleteIfExists(Path.of(unsupportedContentTypeFile.getAbsolutePath()));
	}

	@Test
	@DisplayName("빈 이미지 파일을 업로드하면 예외가 발생한다.")
	void testEmptyImageFile() throws IOException {
		MultipartFile testFile = getTestMultipartFile(emptyFile);

		assertThatThrownBy(() -> ImageFileValidator.validateImageFile(testFile))
			.isInstanceOf(FileException.class);
	}

	@Test
	@DisplayName("지원하지 않는 형식의 이미지 파일 형식을 업로드하면 예외가 발생한다.")
	void testUnsupportedContentType() throws IOException {
		MultipartFile testFile = getTestMultipartFile(unsupportedContentTypeFile);

		assertThatThrownBy(() -> ImageFileValidator.validateImageFile(testFile))
			.isInstanceOf(FileException.class);
	}

	public MultipartFile getTestMultipartFile(File file) throws IOException {
		FileInputStream fis = new FileInputStream(file);

		return new MockMultipartFile(file.getName(), fis);
	}
}
