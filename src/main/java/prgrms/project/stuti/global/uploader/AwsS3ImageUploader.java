package prgrms.project.stuti.global.uploader;

import java.util.Collections;
import java.util.List;

import org.apache.logging.log4j.util.Strings;
import org.springframework.web.multipart.MultipartFile;

import prgrms.project.stuti.global.uploader.common.ImageDirectory;

//TODO: PROD 브랜치 구현 완료
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
	public void delete(String imageUrl) {
	}
}
