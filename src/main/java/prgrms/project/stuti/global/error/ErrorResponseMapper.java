package prgrms.project.stuti.global.error;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import prgrms.project.stuti.global.error.dto.ErrorCode;
import prgrms.project.stuti.global.error.dto.ErrorResponse;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ErrorResponseMapper {

	public static ResponseEntity<ErrorResponse> toErrorResponse(ErrorCode errorCode) {
		return ResponseEntity.status(errorCode.getHttpStatus()).body(ErrorResponse.of(errorCode));
	}

	public static ResponseEntity<ErrorResponse> toErrorResponse(ErrorCode errorCode, BindingResult bindingResult) {
		return ResponseEntity.status(errorCode.getHttpStatus()).body(ErrorResponse.of(errorCode, bindingResult));
	}
}
