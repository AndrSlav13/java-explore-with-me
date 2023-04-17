package ru.practicum.explorewithme.exceptions;

import org.springframework.http.HttpStatus;

public class WrongEnumStatusException extends HttpCustomException {
    public WrongEnumStatusException(HttpStatus status, String message) {
        super(status, message);
    }
}
