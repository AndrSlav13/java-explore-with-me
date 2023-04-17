package ru.practicum.explorewithme.event.dto.constraints;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = LocationConstrainValidator.class)
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface LocationConstrain {
    String message() default "Fields values don't match!";

    String lat() default "lat";

    String lon() default "lon";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}