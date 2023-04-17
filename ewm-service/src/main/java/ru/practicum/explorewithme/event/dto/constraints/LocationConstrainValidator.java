package ru.practicum.explorewithme.event.dto.constraints;

import org.springframework.beans.BeanWrapperImpl;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class LocationConstrainValidator
        implements ConstraintValidator<LocationConstrain, Object> {

    private String lat;
    private String lon;

    public void initialize(LocationConstrain constraintAnnotation) {
        this.lat = constraintAnnotation.lat();
        this.lon = constraintAnnotation.lon();
    }

    public boolean isValid(Object value,
                           ConstraintValidatorContext context) {

        Object fieldValueLat = new BeanWrapperImpl(value)
                .getPropertyValue(lat);
        Object fieldValueLon = new BeanWrapperImpl(value)
                .getPropertyValue(lon);

        if (fieldValueLat == null ||
                fieldValueLon == null) return false;

        String latS = (String) fieldValueLat;
        String lonS = (String) fieldValueLon;

        if (!latS.matches("-?\\d*\\.?\\d*") ||
                !lonS.matches("-?\\d*\\.?\\d*")) return false;

        Float latI = (Float) fieldValueLat;
        Float lonI = (Float) fieldValueLon;

        if (latI <= 90 && latI >= -90 &&
                lonI <= 180 && lonI >= -180) return true;

        return false;
    }
}