/*
https://www.baeldung.com/javax-validations-enums
**/
package ru.practicum.explorewithme.event.dto.constraints;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({METHOD})
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = DateConstrainValidator.class)
public @interface DateConstrain {
    String regexp() default "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}$";

    String message() default "must match \"{regexp}\" and belong to ENUM \"StatusItem\"";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}