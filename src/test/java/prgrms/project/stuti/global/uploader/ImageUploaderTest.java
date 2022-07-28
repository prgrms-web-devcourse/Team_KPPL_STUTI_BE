package prgrms.project.stuti.global.uploader;

import static org.assertj.core.api.Assertions.*;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import javax.imageio.ImageIO;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import net.coobird.thumbnailator.name.Rename;

import prgrms.project.stuti.global.uploader.dto.ImageDeleteDto;
import prgrms.project.stuti.global.uploader.dto.ImageUploadAllDto;
import prgrms.project.stuti.global.uploader.dto.ImageUploadDto;
import prgrms.project.stuti.global.uploader.dto.ThumbnailCreateDto;

@SpringBootTest
class ImageUploaderTest {

	@Autowired
	private ImageUploader imageUploader;

	@Autowired
	private ResourceLoader resourceLoader;

	private String rootPath;
	private File testImageFile;
	private File imageFile;
	private final int width = 200;
	private final int height = 200;

	@BeforeEach
	void setup() throws IOException {
		String testFilePath = Paths.get("src", "test", "resources").toString();
		rootPath = resourceLoader.getResource("classpath:static").getURL().getPath();
		testImageFile = new File(testFilePath + File.separator + "test.png");

		assertThat(testImageFile).isFile();
	}

	@AfterEach
	void cleanup() throws IOException {
		Files.deleteIfExists(Paths.get(imageFile.getAbsolutePath()));
	}

	@Test
	@DisplayName("이미지 파일을 입력된 사이즈에 맞게 리사이징하고 업로드한다.")
	void testUpload() throws IOException {
		MultipartFile testMultipartFile = getMockMultipartFile(testImageFile);
		ImageUploadDto uploadDto = new ImageUploadDto(testMultipartFile, width, height);

		String imageFilePath = imageUploader.upload(uploadDto, ImageDirectory.STUDY_GROUP);

		assertThat(imageFilePath).isNotNull();

		String fullPath = rootPath + File.separator + imageFilePath;
		imageFile = new File(fullPath);

		assertThat(imageFile).isFile();

		BufferedImage bufferedImage = ImageIO.read(imageFile);

		assertThat(bufferedImage.getWidth()).isEqualTo(width);
		assertThat(bufferedImage.getHeight()).isEqualTo(height);
	}

	@Test
	@DisplayName("여러개의 이미지 파일을 입력된 사이즈에 맞게 리사이징하고 업로드한다.")
	void testUploadAll() throws IOException {
		MultipartFile testMultipartFile = getMockMultipartFile(testImageFile);
		MultipartFile testMultipartFile2 = getMockMultipartFile(testImageFile);
		ImageUploadAllDto uploadAllDto = new ImageUploadAllDto(List.of(testMultipartFile, testMultipartFile2), width,
			height);

		List<String> imageFilePaths = imageUploader.uploadAll(uploadAllDto, ImageDirectory.STUDY_GROUP);

		assertThat(imageFilePaths).isNotNull();
		assertThat(imageFilePaths.size()).isNotZero();

		List<String> fullPaths = imageFilePaths.stream().map(i -> rootPath + File.separator + i).toList();

		for (String fullPath : fullPaths) {
			imageFile = new File(fullPath);
			assertThat(imageFile).isFile();

			BufferedImage bufferedImage = ImageIO.read(imageFile);

			assertThat(bufferedImage.getWidth()).isEqualTo(width);
			assertThat(bufferedImage.getHeight()).isEqualTo(height);

			Files.deleteIfExists(Paths.get(imageFile.getAbsolutePath()));
		}
	}

	@Test
	@DisplayName("업로드된 이미지파일 중 첫 번째 이미지 파일을 썸네일로 만든다.")
	void testCreateThumbnail() throws IOException {
		int thumbnailHeight = 100;
		int thumbnailWidth = 100;
		MultipartFile testMultipartFile = getMockMultipartFile(testImageFile);
		ImageUploadDto uploadDto = new ImageUploadDto(testMultipartFile, width, height);

		String imageFilePath = imageUploader.upload(uploadDto, ImageDirectory.STUDY_GROUP);

		assertThat(imageFilePath).isNotNull();

		String fullPath = rootPath + File.separator + imageFilePath;
		imageFile = new File(fullPath);

		assertThat(imageFile).isFile();

		imageUploader.createThumbnail(new ThumbnailCreateDto(imageFilePath, thumbnailWidth, thumbnailHeight));

		String thumbnailFilePath = Rename.SUFFIX_DOT_THUMBNAIL.apply(imageFilePath, null);
		String thumbnailFullPath = rootPath + File.separator + thumbnailFilePath;
		File thumbnailImageFile = new File(thumbnailFullPath);

		assertThat(thumbnailImageFile).isFile();

		BufferedImage bufferedImage = ImageIO.read(thumbnailImageFile);

		assertThat(bufferedImage.getWidth()).isEqualTo(thumbnailWidth);
		assertThat(bufferedImage.getHeight()).isEqualTo(thumbnailHeight);

		Files.deleteIfExists(Paths.get(rootPath, thumbnailFilePath));
	}

	@Test
	@DisplayName("업로드된 이미지 파일을 삭제한다.")
	void testDelete() throws IOException {
		MultipartFile testMultipartFile = getMockMultipartFile(testImageFile);
		ImageUploadDto uploadDto = new ImageUploadDto(testMultipartFile, width, height);

		String imageFilePath = imageUploader.upload(uploadDto, ImageDirectory.STUDY_GROUP);

		assertThat(imageFilePath).isNotNull();

		String fullPath = rootPath + File.separator + imageFilePath;
		imageFile = new File(fullPath);

		assertThat(imageFile).isFile();

		imageUploader.delete(new ImageDeleteDto(imageFilePath));

		assertThat(imageFile.isFile()).isFalse();
	}

	private MultipartFile getMockMultipartFile(File testFile) throws IOException {
		FileInputStream inputStream = new FileInputStream(testFile);
		String[] split = testFile.getName().split("\\.");

		return new MockMultipartFile(split[0], testFile.getName(), "image/" + split[1], inputStream);
	}
}
