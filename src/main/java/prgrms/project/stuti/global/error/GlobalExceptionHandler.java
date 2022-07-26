package prgrms.project.stuti.global.error;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;
import prgrms.project.stuti.global.error.dto.ErrorCode;
import prgrms.project.stuti.global.error.dto.ErrorResponse;
import prgrms.project.stuti.global.error.exception.BusinessException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	protected ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex,
		BindingResult bindingResult) {
		log.info("Got MethodArgumentNotValidException: {}", ex.getMessage(), ex);

		return null;
	}

	@ExceptionHandler(BusinessException.class)
	protected ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex) {
		log.info("Got BusinessException: {}", ex.getMessage(), ex);

		return ErrorResponseMapper.toErrorResponse(ex.getErrorCode());
	}

	@ExceptionHandler(Exception.class)
	protected ResponseEntity<ErrorResponse> handleException(Exception ex) {
		log.error("Got Exception: {}", ex.getMessage(), ex);

		return ErrorResponseMapper.toErrorResponse(ErrorCode.UNKNOWN_SERVER_ERROR);
	}
}
