package prgrms.project.stuti.global.uploader;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.apache.logging.log4j.util.Strings;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import prgrms.project.stuti.global.error.exception.FileException;
import prgrms.project.stuti.global.uploader.common.ImageDirectory;
import prgrms.project.stuti.global.uploader.common.ImageFileUtils;

@Component
public record LocalImageUploader(ResourceLoader resourceLoader) implements ImageUploader {

	private static final String DEFAULT_CLASS_PATH = "classpath:static";

	@Override
	public String upload(MultipartFile multipartFile, ImageDirectory imageDirectory) {
		Resource resource = resourceLoader.getResource(DEFAULT_CLASS_PATH);

		if (multipartFile == null || multipartFile.isEmpty()) {
			return Strings.EMPTY;
		}

		return storeImageFile(multipartFile, imageDirectory, resource);
	}

	@Override
	public List<String> uploadAll(List<MultipartFile> multipartFiles, ImageDirectory imageDirectory) {
		Resource resource = resourceLoader.getResource(DEFAULT_CLASS_PATH);
		List<String> imageFileUrls = Collections.synchronizedList(new ArrayList<>());

		if (multipartFiles == null || multipartFiles.isEmpty()) {
			return List.of(Strings.EMPTY);
		}

		for (MultipartFile multipartFile : multipartFiles) {
			String imageFileUrl = storeImageFile(multipartFile, imageDirectory, resource);
			imageFileUrls.add(imageFileUrl);
		}

		return imageFileUrls;
	}

	@Override
	public void delete(String imageUrl) {
		Resource resource = resourceLoader.getResource(DEFAULT_CLASS_PATH);

		try {
			URL rootUrl = resource.getURL();

			Files.deleteIfExists(Paths.get(rootUrl.getPath(), imageUrl));
		} catch (IOException ex) {
			throw FileException.failedToDelete(ex);
		}
	}

	private String getDirectoryPath(String rootPath, String directory) {
		return Paths.get(rootPath, directory).toString();
	}

	private String getImageFileUrl(String directory, String name) {
		return Paths.get(File.separator, directory, name).toString();
	}

	private File createImageFile(MultipartFile multipartFile, File directory) {
		String extension = ImageFileUtils.getExtension(Objects.requireNonNull(multipartFile.getContentType()));
		String newName = ImageFileUtils.rename(extension);

		return new File(getImageFileUrl(directory.getAbsolutePath(), newName));
	}

	private String storeImageFile(MultipartFile multipartFile, ImageDirectory imageDirectory, Resource resource) {
		try {
			URL rootUrl = resource.getURL();
			File directory = makeDirectory(getDirectoryPath(rootUrl.getPath(), imageDirectory.getDirectory()));
			File imageFile = createImageFile(multipartFile, directory);

			multipartFile.transferTo(imageFile);

			return getImageFileUrl(imageDirectory.getDirectory(), imageFile.getName());
		} catch (IOException ex) {
			throw FileException.failedToUpload(ex);
		}
	}

	private File makeDirectory(String fullPath) {
		File directory = new File(fullPath);

		if (!directory.exists()) {
			directory.mkdirs();
		}

		return directory;
	}
}
