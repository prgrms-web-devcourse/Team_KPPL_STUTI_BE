package prgrms.project.stuti.global.uploader;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import prgrms.project.stuti.global.uploader.common.ImageDirectory;

public interface ImageUploader {

	String upload(MultipartFile multipartFile, ImageDirectory imageDirectory);

	List<String> uploadAll(List<MultipartFile> multipartFiles, ImageDirectory imageDirectory);

	String createThumbnail(String imageUrl);

	void delete(String imageUrl);
}
