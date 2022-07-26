package prgrms.project.stuti.global.error.dto;

import java.util.Collections;
import java.util.List;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

public record CustomFieldError(String field, Object value, String cause) {

	public static List<CustomFieldError> from(BindingResult bindingResult) {
		final List<FieldError> fieldErrors = bindingResult.getFieldErrors();

		if (fieldErrors.isEmpty()) {
			return Collections.emptyList();
		}

		return fieldErrors.stream()
			.map(e -> new CustomFieldError(e.getField(), e.getRejectedValue(), e.getDefaultMessage()))
			.toList();
	}
}
