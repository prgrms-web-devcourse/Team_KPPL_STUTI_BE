package prgrms.project.stuti.global.uploader.dto;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public record ImageUploadAllDto(List<MultipartFile> multipartFiles, int width, int height) {
}
