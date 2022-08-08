package prgrms.project.stuti.global.uploader.common;

import java.util.UUID;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ImageFileUtils {

	public static String rename(String extension) {
		return UUID.randomUUID() + "." + extension;
	}

	public static String getExtension(String contentType) {
		int slashIndex = contentType.lastIndexOf("/");

		return contentType.substring(slashIndex + 1).toUpperCase();
	}
}
