package prgrms.project.stuti.global.uploader;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import prgrms.project.stuti.global.error.exception.FileException;
import prgrms.project.stuti.global.uploader.dto.ImageUploadAllDto;
import prgrms.project.stuti.global.uploader.dto.ImageUploadDto;
import prgrms.project.stuti.global.uploader.dto.ThumbnailCreateDto;

@Service
public record LocalImageUploader(ResourceLoader resourceLoader) implements ImageUploader {

	private static final String DEFAULT_CLASS_PATH = "classpath:static";

	@Override
	public String upload(ImageUploadDto uploadDto, ImageDirectory imageDirectory) {
		Resource resource = resourceLoader.getResource(DEFAULT_CLASS_PATH);
		MultipartFile multipartFile = uploadDto.multipartFile();
		int width = uploadDto.width();
		int height = uploadDto.height();

		ImageFileValidator.validateImageFile(multipartFile);

		try {
			URL rootPath = resource.getURL();
			File directory =
				ImageFileUtils.makeDirectory(getDirectoryPath(rootPath.getPath(), imageDirectory.getDirectory()));
			File imageFile = createImageFile(multipartFile, directory);

			multipartFile.transferTo(imageFile);
			ImageFileUtils.resize(directory, imageFile, width, height);

			return getImageFilePath(imageDirectory.getDirectory(), imageFile.getName());
		} catch (IOException ex) {
			FileException.FAILED_TO_UPLOAD.accept(ex);
		}

		return StringUtils.EMPTY;
	}


	@Override
	public List<String> uploadAll(ImageUploadAllDto uploadAllDto, ImageDirectory imageDirectory) {
		Resource resource = resourceLoader.getResource(DEFAULT_CLASS_PATH);
		List<String> imageFilePaths = Collections.synchronizedList(new ArrayList<>());
		List<MultipartFile> multipartFiles = uploadAllDto.multipartFiles();
		int width = uploadAllDto.width();
		int height = uploadAllDto.height();

		for (MultipartFile multipartFile : multipartFiles) {
			ImageFileValidator.validateImageFile(multipartFile);

			try {
				URL rootPath = resource.getURL();
				File directory =
					ImageFileUtils.makeDirectory(getDirectoryPath(rootPath.getPath(), imageDirectory.getDirectory()));
				File imageFile = createImageFile(multipartFile, directory);

				multipartFile.transferTo(imageFile);
				ImageFileUtils.resize(directory, imageFile, width, height);

				imageFilePaths.add(getImageFilePath(imageDirectory.getDirectory(), imageFile.getName()));
			} catch (IOException ex) {
				FileException.FAILED_TO_UPLOAD.accept(ex);
			}
		}

		return imageFilePaths;
	}

	@Override
	public void createThumbnail(ThumbnailCreateDto createDto) {
		Resource resource = resourceLoader.getResource(DEFAULT_CLASS_PATH);

		try {
			URL rootPath = resource.getURL();
			File imageFile = new File(getImageFilePath(rootPath.getPath(), createDto.imageUrl()));
			int width = createDto.width();
			int height = createDto.height();
			String fullPath = imageFile.getParent() + File.separator;
			File directory = ImageFileUtils.makeDirectory(fullPath);

			ImageFileUtils.createThumbnail(directory, imageFile, width, height);
		} catch (IOException ex) {
			FileException.FAILED_TO_UPLOAD.accept(ex);
		}
	}

	private String getDirectoryPath(String rootPath, String directory) {
		return Paths.get(rootPath, directory).toString();
	}

	private String getImageFilePath(String directory, String name) {
		return Paths.get(directory, name).toString();
	}

	private File createImageFile(MultipartFile multipartFile, File directory) {
		String extension = ImageFileUtils.getExtension(Objects.requireNonNull(multipartFile.getContentType()));
		String newName = ImageFileUtils.rename(extension);

		return new File(getImageFilePath(directory.getAbsolutePath(), newName));
	}
}
