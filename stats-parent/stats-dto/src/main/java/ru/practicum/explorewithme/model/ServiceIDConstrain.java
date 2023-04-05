/*
https://www.baeldung.com/javax-validations-enums
**/
package ru.practicum.explorewithme.model;

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
@Constraint(validatedBy = ServiceIDConstrainValidator.class)
public @interface ServiceIDConstrain {
    String regexp() default "*";

    String message() default "wrong service name";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}