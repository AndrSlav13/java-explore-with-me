/*
https://www.baeldung.com/javax-validations-enums
**/
package ru.practicum.explorewithme.event.dto.constraints;

import ru.practicum.explorewithme.event.model.StateActionEvent;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;

public class EnumStateActionEventConstrainValidator implements ConstraintValidator<EnumStateActionEventConstrain, String> {

    private StateActionEvent[] values;

    public void initialize(EnumStateActionEventConstrain constraintAnnotation) {
        this.values = constraintAnnotation.values();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        return Arrays.stream(values).anyMatch(val -> val.name().equals(value));
    }
}