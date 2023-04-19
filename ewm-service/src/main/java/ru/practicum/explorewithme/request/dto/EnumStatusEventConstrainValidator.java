/*
https://www.baeldung.com/javax-validations-enums
**/
package ru.practicum.explorewithme.request.dto;

import ru.practicum.explorewithme.request.model.StatusEventParticipation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;

public class EnumStatusEventConstrainValidator implements ConstraintValidator<EnumStatusEventConstrain, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        return Arrays.stream(StatusEventParticipation.values()).filter(a -> !a.equals(StatusEventParticipation.PENDING))
                .anyMatch(val -> val.name().equals(value));
    }
}