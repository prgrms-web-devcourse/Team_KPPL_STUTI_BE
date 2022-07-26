package prgrms.project.stuti.global.error.dto;

import java.util.Collections;
import java.util.List;

import org.springframework.validation.BindingResult;

public record ErrorResponse(String errorCode, String message, List<CustomFieldError> errors) {

	public static ErrorResponse of(ErrorCode errorCode) {
		return new ErrorResponse(errorCode.getCode(), errorCode.getMessage(), Collections.emptyList());
	}

	public static ErrorResponse of(ErrorCode errorCode, BindingResult bindingResult) {
		return new ErrorResponse(errorCode.getCode(), errorCode.getMessage(), CustomFieldError.from(bindingResult));
	}
}
