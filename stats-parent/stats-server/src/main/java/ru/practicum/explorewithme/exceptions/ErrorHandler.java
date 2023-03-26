package ru.practicum.explorewithme.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
        log.warn("400", e);
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    public ErrorResponse handleWrongEnum(HttpServletResponse resp, final WrongEnumStatusException e) {
        log.warn(e.getMessage(), e);
        resp.setStatus(e.getCode());
        return new ErrorResponse(e.getMessageWithoutCode());
    }

    @ExceptionHandler
    public ErrorResponse handleCustom(HttpServletResponse resp, final HttpCustomException e) {
        log.warn(e.getMessage(), e);
        resp.setStatus(e.getCode());
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleThrowable(final Throwable e) {
        log.warn("500", e);
        return new ErrorResponse("Произошла непредвиденная ошибка сервера" + Arrays.toString(e.getStackTrace()));
    }
}
