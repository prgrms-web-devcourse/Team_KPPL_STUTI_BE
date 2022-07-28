package prgrms.project.stuti.global.uploader;

import java.util.Collections;
import java.util.List;

import prgrms.project.stuti.global.uploader.dto.ImageDeleteDto;
import prgrms.project.stuti.global.uploader.dto.ImageUploadAllDto;
import prgrms.project.stuti.global.uploader.dto.ImageUploadDto;
import prgrms.project.stuti.global.uploader.dto.ThumbnailCreateDto;

//TODO: 2022/07/27 배포 환경에서 사용
public class AwsS3ImageUploader implements ImageUploader {

	@Override
	public String upload(ImageUploadDto uploadDto, ImageDirectory imageDirectory) {
		return null;
	}

	@Override
	public List<String> uploadAll(ImageUploadAllDto uploadAllDto, ImageDirectory imageDirectory) {
		return Collections.emptyList();
	}

	@Override
	public void createThumbnail(ThumbnailCreateDto createDto) {
	}

	@Override
	public void delete(ImageDeleteDto deleteDto) {
	}
}
