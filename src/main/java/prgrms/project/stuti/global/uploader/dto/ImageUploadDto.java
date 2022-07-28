package prgrms.project.stuti.global.uploader.dto;

import org.springframework.web.multipart.MultipartFile;

public record ImageUploadDto(MultipartFile multipartFile, int width, int height) {
}
