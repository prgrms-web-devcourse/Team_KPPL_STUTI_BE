package prgrms.project.stuti.global.error.exception;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.function.Consumer;
import java.util.function.Supplier;

import prgrms.project.stuti.global.error.dto.ErrorCode;

public class FileException extends BusinessException {

	protected FileException(ErrorCode errorCode, String message) {
		super(errorCode, message);
	}

	protected FileException(ErrorCode errorCode, String message, Throwable cause) {
		super(errorCode, message, cause);
	}

	public static final Supplier<String> EMPTY_FILE = () -> {
		throw new FileException(ErrorCode.EMPTY_FILE, "파일 정보가 존재하지않습니다.");
	};

	public static final Consumer<String> UNSUPPORTED_EXTENSION = contentType -> {
		throw new FileException(ErrorCode.UNSUPPORTED_EXTENSION,
			MessageFormat.format("지원하지 않는 파일형식입니다. ({0})", contentType));
	};

	public static final Consumer<IOException> FAILED_TO_RESIZE = ex -> {
		throw new FileException(ErrorCode.FAILED_RESIZE, "이미지 파일의 리사이즈가 실패하였습니다.", ex);
	};

	public static final Consumer<IOException> FAILED_TO_UPLOAD = ex -> {
		throw new FileException(ErrorCode.FAILED_UPLOAD, "이미지를 파일의 업로드가 실패하였습니다.", ex);
	};

	public static final Consumer<IOException> FAILED_TO_DELETE = ex -> {
		throw new FileException(ErrorCode.FAILED_DELETE, "이미지를 파일의 삭제가 실패하였습니다.", ex);
	};
}
