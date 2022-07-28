package prgrms.project.stuti.global.uploader;

import java.util.List;

import prgrms.project.stuti.global.uploader.dto.ImageDeleteDto;
import prgrms.project.stuti.global.uploader.dto.ImageUploadAllDto;
import prgrms.project.stuti.global.uploader.dto.ImageUploadDto;
import prgrms.project.stuti.global.uploader.dto.ThumbnailCreateDto;

public interface ImageUploader {

	String upload(ImageUploadDto uploadDto, ImageDirectory imageDirectory);

	List<String> uploadAll(ImageUploadAllDto uploadAllDto, ImageDirectory imageDirectory);

	void createThumbnail(ThumbnailCreateDto createDto);

	void delete(ImageDeleteDto deleteDto);
}
