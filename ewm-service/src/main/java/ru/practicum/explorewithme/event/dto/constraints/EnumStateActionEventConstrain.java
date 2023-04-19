/*
https://www.baeldung.com/javax-validations-enums
**/
package ru.practicum.explorewithme.event.dto.constraints;

import ru.practicum.explorewithme.event.model.StateActionEvent;

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
@Constraint(validatedBy = EnumStateActionEventConstrainValidator.class)
public @interface EnumStateActionEventConstrain {
    String regexp() default "*";

    String message() default "must match \"{regexp}\" and belong to ENUM \"StatusItem\"";

    StateActionEvent[] values() default {};

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}