package prgrms.project.stuti.domain.studygroup.controller.dto.validator;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import org.hibernate.validator.constraints.Length;

@Documented
@Constraint(validatedBy = { })
@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE })
@Retention(RUNTIME)
@Length(min = 5, max = 50)
public @interface TitleLength {

	String message() default "{org.hibernate.validator.constraints.Length.message}";

	Class<?>[] groups() default { };

	Class<? extends Payload>[] payload() default { };
}
