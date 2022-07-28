package prgrms.project.stuti.global.uploader;

import java.util.Collections;
import java.util.List;

import org.apache.logging.log4j.util.Strings;
import org.springframework.web.multipart.MultipartFile;

import prgrms.project.stuti.global.uploader.common.ImageDirectory;

//TODO: 2022/07/27 배포 환경에서 사용
public class AwsS3ImageUploader implements ImageUploader {

	@Override
	public String upload(MultipartFile multipartFile, ImageDirectory imageDirectory) {
		return Strings.EMPTY;
	}

	@Override
	public List<String> uploadAll(List<MultipartFile> multipartFiles, ImageDirectory imageDirectory) {
		return Collections.emptyList();
	}

	@Override
	public String createThumbnail(String imageUrl) {
		return Strings.EMPTY;
	}

	@Override
	public void delete(String imageUrl) {
	}
}
