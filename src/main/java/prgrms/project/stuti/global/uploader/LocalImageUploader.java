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
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import prgrms.project.stuti.global.error.exception.FileException;
import prgrms.project.stuti.global.uploader.common.ImageDirectory;
import prgrms.project.stuti.global.uploader.common.ImageFileUtils;

@Service
public record LocalImageUploader(ResourceLoader resourceLoader) implements ImageUploader {

	private static final String DEFAULT_CLASS_PATH = "classpath:static";
	private static final String BASIC_IMAGE_URL = "/images/basic/basic.PNG";
	private static final String BASIC_THUMBNAIL_URL = "/images/basic/basic.PNG";

	@Override
	public String upload(MultipartFile multipartFile, ImageDirectory imageDirectory) {
		Resource resource = resourceLoader.getResource(DEFAULT_CLASS_PATH);

		if (multipartFile == null || multipartFile.isEmpty()) {
			return BASIC_IMAGE_URL;
		}

		return storeImageFile(multipartFile, imageDirectory, resource);
	}

	@Override
	public List<String> uploadAll(List<MultipartFile> multipartFiles, ImageDirectory imageDirectory) {
		Resource resource = resourceLoader.getResource(DEFAULT_CLASS_PATH);
		List<String> imageFileUrls = Collections.synchronizedList(new ArrayList<>());

		if (multipartFiles.isEmpty()) {
			return List.of(BASIC_IMAGE_URL);
		}

		for (MultipartFile multipartFile : multipartFiles) {
			String imageFileUrl = storeImageFile(multipartFile, imageDirectory, resource);
			imageFileUrls.add(imageFileUrl);
		}

		return imageFileUrls;
	}

	@Override
	public String createThumbnail(String imageUrl) {
		Resource resource = resourceLoader.getResource(DEFAULT_CLASS_PATH);

		if (imageUrl.equals(BASIC_IMAGE_URL)) {
			return BASIC_THUMBNAIL_URL;
		}

		try {
			URL rootUrl = resource.getURL();
			String rootPath = rootUrl.getPath();
			String imageFileUrl = Paths.get(rootPath, imageUrl).toString();

			String thumbnailFileUrl = ImageFileUtils.createThumbnail(new File(imageFileUrl));

			return getThumbnailFileUrl(rootPath, thumbnailFileUrl);
		} catch (IOException ex) {
			throw FileException.failedToCreateThumbnail(ex);
		}
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

	private String getThumbnailFileUrl(String rootPath, String thumbnailFullPath) {
		return thumbnailFullPath.replace(rootPath, Strings.EMPTY);
	}

	private String storeImageFile(MultipartFile multipartFile, ImageDirectory imageDirectory, Resource resource) {
		try {
			URL rootUrl = resource.getURL();
			File directory =
				ImageFileUtils.makeDirectory(getDirectoryPath(rootUrl.getPath(), imageDirectory.getDirectory()));
			File imageFile = createImageFile(multipartFile, directory);

			multipartFile.transferTo(imageFile);

			return getImageFileUrl(imageDirectory.getDirectory(), imageFile.getName());
		} catch (IOException ex) {
			throw FileException.failedToUpload(ex);
		}
	}
}
