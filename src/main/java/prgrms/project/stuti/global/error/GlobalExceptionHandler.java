package prgrms.project.stuti.global.error;

import java.sql.SQLIntegrityConstraintViolationException;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MultipartException;

import lombok.extern.slf4j.Slf4j;
import prgrms.project.stuti.global.error.dto.ErrorCode;
import prgrms.project.stuti.global.error.dto.ErrorResponse;
import prgrms.project.stuti.global.error.exception.BusinessException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(BindException.class)
	protected ResponseEntity<ErrorResponse> handleBindException(BindException ex,
		BindingResult bindingResult) {
		log.info("Got BindException: {}", ex.getMessage(), ex);

		return ErrorResponseMapper.toErrorResponse(ErrorCode.INVALID_METHOD_ARGUMENT, bindingResult);
	}

	@ExceptionHandler(BusinessException.class)
	protected ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex) {
		log.info("Got BusinessException: {}", ex.getMessage(), ex);

		return ErrorResponseMapper.toErrorResponse(ex.getErrorCode());
	}

	@ExceptionHandler(MultipartException.class)
	protected ResponseEntity<ErrorResponse> handleBusinessException(MultipartException ex) {
		log.info("Got MultipartException: {}", ex.getMessage(), ex);

		return ErrorResponseMapper.toErrorResponse(ErrorCode.OVER_MAX_SIZE);
	}

	@ExceptionHandler(Exception.class)
	protected ResponseEntity<ErrorResponse> handleException(Exception ex) {
		log.error("Got Exception: {}", ex.getMessage(), ex);

		return ErrorResponseMapper.toErrorResponse(ErrorCode.UNKNOWN_SERVER_ERROR);
	}
}
