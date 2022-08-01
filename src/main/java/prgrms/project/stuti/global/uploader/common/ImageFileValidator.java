package prgrms.project.stuti.global.uploader.common;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import prgrms.project.stuti.global.error.exception.FileException;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ImageFileValidator {

	private static final List<String> SUPPORTED_CONTENT_TYPES = List.of("image/png", "image/jpeg", "image/jpg");

	public static void validateImageFile(MultipartFile imageFile) {
		validateEmptyValue(imageFile);
		validateContentType(imageFile);
	}

	private static void validateEmptyValue(MultipartFile imageFile) {
		if (imageFile.isEmpty()) {
			throw FileException.emptyFile();
		}
	}

	private static void validateContentType(MultipartFile imageFile) {
		String contentType = imageFile.getContentType();
		boolean isSupported = SUPPORTED_CONTENT_TYPES.contains(contentType);

		if (!isSupported) {
			throw FileException.unsupportedExtension(imageFile.getContentType());
		}
	}
}
