/*
https://www.baeldung.com/javax-validations-enums
**/
package ru.practicum.explorewithme.event.dto.constraints;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class DateConstrainValidator implements ConstraintValidator<DateConstrain, String> {
    private String regexp;

    public void initialize(DateConstrain constraintAnnotation) {
        this.regexp = constraintAnnotation.regexp();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.matches(regexp)) {
            return true;
        }

        return false;
    }
}