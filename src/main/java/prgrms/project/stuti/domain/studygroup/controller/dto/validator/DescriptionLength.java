package prgrms.project.stuti.domain.studygroup.controller.dto.validator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import org.hibernate.validator.constraints.Length;

@Documented
@Constraint(validatedBy = { })
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Length(min = 10, max = 1000)
public @interface DescriptionLength {

	String message() default "{org.hibernate.validator.constraints.Length.message}";

	Class<?>[] groups() default { };

	Class<? extends Payload>[] payload() default { };
}
