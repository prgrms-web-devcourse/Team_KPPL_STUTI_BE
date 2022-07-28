package prgrms.project.stuti.global.uploader;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.UUID;

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.name.Rename;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import prgrms.project.stuti.global.error.exception.FileException;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ImageFileUtils {

	private static final double OUTPUT_QUALITY = 0.9;

	public static String rename(String extension) {
		return UUID.randomUUID() + "." + extension;
	}

	public static String getExtension(String contentType) {
		int slashIndex = contentType.lastIndexOf("/");

		return contentType.substring(slashIndex + 1).toUpperCase(Locale.ROOT);
	}

	public static File makeDirectory(String fullPath) {
		File directory = new File(fullPath);

		if (!directory.exists()) {
			directory.mkdirs();
		}

		return directory;
	}

	public static void resize(File directory, File imageFile, int width, int height) {
		try {
			Thumbnails.of(imageFile)
				.outputQuality(OUTPUT_QUALITY)
				.forceSize(width, height)
				.toFiles(directory, Rename.NO_CHANGE);
		} catch (IOException ex) {
			FileException.FAILED_TO_RESIZE.accept(ex);
		}
	}

	public static void createThumbnail(File directory, File imageFile, int width, int height) {
		try {
			Thumbnails.of(imageFile)
				.outputQuality(OUTPUT_QUALITY)
				.forceSize(width, height)
				.toFiles(directory, Rename.SUFFIX_DOT_THUMBNAIL);
		} catch (IOException ex) {
			FileException.FAILED_TO_RESIZE.accept(ex);
		}
	}
}
