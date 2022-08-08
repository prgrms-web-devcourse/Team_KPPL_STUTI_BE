package prgrms.project.stuti.global.error.exception;

import java.io.IOException;
import java.text.MessageFormat;

import prgrms.project.stuti.global.error.dto.ErrorCode;

public class FileException extends BusinessException {

	protected FileException(ErrorCode errorCode, String message) {
		super(errorCode, message);
	}

	protected FileException(ErrorCode errorCode, String message, Throwable cause) {
		super(errorCode, message, cause);
	}

	public static FileException unsupportedExtension(String contentType) {
		return new FileException(ErrorCode.UNSUPPORTED_EXTENSION,
			MessageFormat.format("지원하지 않는 파일형식입니다. ({0})", contentType));
	}

	public static FileException failedToUpload(IOException ex) {
		throw new FileException(ErrorCode.FAILED_UPLOAD, "이미지 파일 업로드가 실패하였습니다.", ex);
	}

	public static FileException failedToDelete(IOException ex) {
		throw new FileException(ErrorCode.FAILED_DELETE, "이미지를 파일의 삭제가 실패하였습니다.", ex);
	}
}
