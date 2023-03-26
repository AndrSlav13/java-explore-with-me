/*
https://www.baeldung.com/javax-validations-enums
**/
package ru.practicum.explorewithme.model;

import ru.practicum.explorewithme.dto.StatDTO;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ServiceIDConstrainValidator implements ConstraintValidator<ServiceIDConstrain, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return false;

        return StatDTO.serviceIDs.contains(value);
    }
}