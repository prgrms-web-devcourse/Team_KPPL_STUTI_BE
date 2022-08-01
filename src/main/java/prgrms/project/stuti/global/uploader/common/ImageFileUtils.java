package prgrms.project.stuti.global.uploader.common;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.name.Rename;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import prgrms.project.stuti.global.error.exception.FileException;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ImageFileUtils {

	private static final double OUTPUT_QUALITY = 0.9;
	private static final int THUMBNAIL_WIDTH = 320;

	public static String rename(String extension) {
		return UUID.randomUUID() + "." + extension;
	}

	public static String getExtension(String contentType) {
		int slashIndex = contentType.lastIndexOf("/");

		return contentType.substring(slashIndex + 1).toUpperCase();
	}

	public static File makeDirectory(String fullPath) {
		File directory = new File(fullPath);

		if (!directory.exists()) {
			directory.mkdirs();
		}

		return directory;
	}

	public static String createThumbnail(File imageFile) {
		String originName = imageFile.getName();
		String thumbnailName = Rename.PREFIX_DOT_THUMBNAIL.apply(originName, null);
		String thumbnailFullPath = imageFile.getAbsolutePath().replace(originName, thumbnailName);

		try {
			Thumbnails.of(imageFile)
				.outputQuality(OUTPUT_QUALITY)
				.width(THUMBNAIL_WIDTH)
				.toFile(new File(thumbnailFullPath));

			return thumbnailFullPath;
		} catch (IOException ex) {
			throw FileException.failedToCreateThumbnail(ex);
		}
	}
}
