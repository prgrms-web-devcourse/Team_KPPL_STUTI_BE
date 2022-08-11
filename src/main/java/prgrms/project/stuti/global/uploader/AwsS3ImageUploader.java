package prgrms.project.stuti.global.uploader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import lombok.RequiredArgsConstructor;
import prgrms.project.stuti.global.error.exception.FileException;
import prgrms.project.stuti.global.uploader.common.ImageDirectory;
import prgrms.project.stuti.global.uploader.common.ImageFileUtils;

@Primary
@Component
@RequiredArgsConstructor
public class AwsS3ImageUploader implements ImageUploader {

	private final AmazonS3 amazonS3;

	@Value("${cloud.aws.s3.bucket}")
	private String bucket;

	private static final String BEGIN_DIRECTORY = "images";

	@Override
	public String upload(MultipartFile multipartFile, ImageDirectory imageDirectory) {
		if (multipartFile == null || multipartFile.isEmpty()) {
			return Strings.EMPTY;
		}

		String imageFileName = createImageFileName(multipartFile, imageDirectory);
		uploadToS3(multipartFile, imageFileName);

		return getImageUrl(imageFileName);
	}

	@Override
	public List<String> uploadAll(List<MultipartFile> multipartFiles, ImageDirectory imageDirectory) {
		if (multipartFiles == null || multipartFiles.isEmpty()) {
			return List.of(Strings.EMPTY);
		}

		List<String> imageUrls = Collections.synchronizedList(new ArrayList<>());

		for (MultipartFile multipartFile : multipartFiles) {
			String imageFileName = createImageFileName(multipartFile, imageDirectory);
			uploadToS3(multipartFile, imageFileName);

			imageUrls.add(getImageUrl(imageFileName));
		}

		return imageUrls;
	}

	@Override
	public void delete(String imageUrl) {
		String imageFileName = extractImageFileName(imageUrl);
		amazonS3.deleteObject(new DeleteObjectRequest(bucket, imageFileName));
	}

	private String createImageFileName(MultipartFile multipartFile, ImageDirectory imageDirectory) {
		String extension = ImageFileUtils.getExtension(Objects.requireNonNull(multipartFile.getContentType()));

		return imageDirectory.getDirectory() + File.separator + ImageFileUtils.rename(extension);
	}

	private void uploadToS3(MultipartFile multipartFile, String imageFileName) {
		ObjectMetadata objectMetadata = new ObjectMetadata();
		objectMetadata.setContentLength(multipartFile.getSize());
		objectMetadata.setContentType(multipartFile.getContentType());

		try (InputStream inputStream = multipartFile.getInputStream()) {
			amazonS3.putObject(
				new PutObjectRequest(bucket, imageFileName, inputStream, objectMetadata)
					.withCannedAcl(CannedAccessControlList.PublicRead));
		} catch (IOException ex) {
			throw FileException.failedToUpload(ex);
		}
	}

	private String getImageUrl(String imageFileName) {
		return String.valueOf(amazonS3.getUrl(bucket, imageFileName));
	}

	private String extractImageFileName(String imageUrl) {
		int beginIndex = imageUrl.lastIndexOf(BEGIN_DIRECTORY);

		return imageUrl.substring(beginIndex);
	}
}
