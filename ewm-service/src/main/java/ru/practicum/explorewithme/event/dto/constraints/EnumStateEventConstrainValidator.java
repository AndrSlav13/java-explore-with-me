/*
https://www.baeldung.com/javax-validations-enums
**/
package ru.practicum.explorewithme.event.dto.constraints;

import ru.practicum.explorewithme.event.model.StateEvent;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;

public class EnumStateEventConstrainValidator implements ConstraintValidator<EnumStateEventConstrain, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        return Arrays.stream(StateEvent.values()).anyMatch(val -> val.name().equals(value));
    }
}