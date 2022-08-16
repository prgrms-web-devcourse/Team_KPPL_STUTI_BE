package prgrms.project.stuti.global.error;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MultipartException;

import lombok.extern.slf4j.Slf4j;
import prgrms.project.stuti.global.error.dto.ErrorCode;
import prgrms.project.stuti.global.error.dto.ErrorResponse;
import prgrms.project.stuti.global.error.dto.TokenExpirationResponse;
import prgrms.project.stuti.global.error.exception.BusinessException;
import prgrms.project.stuti.global.error.exception.TokenException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(BindException.class)
	protected ResponseEntity<ErrorResponse> handleBindException(BindException ex, BindingResult bindingResult) {
		log.info("Got BindException: {}", ex.getMessage(), ex);

		return ErrorResponseMapper.toErrorResponse(ErrorCode.INVALID_METHOD_ARGUMENT, bindingResult);
	}

	@ExceptionHandler(BusinessException.class)
	protected ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex) {
		log.info("Got BusinessException: {}", ex.getMessage(), ex);

		return ErrorResponseMapper.toErrorResponse(ex.getErrorCode());
	}

	@ExceptionHandler(MultipartException.class)
	protected ResponseEntity<ErrorResponse> handleMultipartException(MultipartException ex) {
		log.info("Got MultipartException: {}", ex.getMessage(), ex);

		return ErrorResponseMapper.toErrorResponse(ErrorCode.OVER_MAX_SIZE);
	}

	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	protected ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupportedException(
		HttpRequestMethodNotSupportedException ex) {
		log.info("Got HttpRequestMethodNotSupportedException: {}", ex.getMessage(), ex);

		return ErrorResponseMapper.toErrorResponse(ErrorCode.METHOD_NOT_ALLOWED);
	}

	@ExceptionHandler(TokenException.class)
	protected ResponseEntity<TokenExpirationResponse> handleTokenExpiration(TokenException ex) {
		log.info("Got TokenExpirationException. tokenValue : {}", ex.getMessage(), ex);

		return ErrorResponseMapper.toTokenExpirationResponse(ex.getErrorCode());
	}

	@ExceptionHandler(Exception.class)
	protected ResponseEntity<ErrorResponse> handleException(Exception ex) {
		log.error("Got Exception: {}", ex.getMessage(), ex);

		return ErrorResponseMapper.toErrorResponse(ErrorCode.UNKNOWN_SERVER_ERROR);
	}
}
