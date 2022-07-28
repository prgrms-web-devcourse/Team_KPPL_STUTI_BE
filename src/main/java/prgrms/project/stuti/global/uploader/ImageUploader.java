package prgrms.project.stuti.global.uploader;

import java.util.List;

import prgrms.project.stuti.global.uploader.dto.ImageUploadAllDto;
import prgrms.project.stuti.global.uploader.dto.ImageUploadDto;

public interface ImageUploader {

	String upload(ImageUploadDto uploadDto, ImageDirectory imageDirectory);

	List<String> uploadAll(ImageUploadAllDto uploadAllDto, ImageDirectory imageDirectory);
}
