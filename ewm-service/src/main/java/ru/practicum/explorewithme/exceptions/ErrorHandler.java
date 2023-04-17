package ru.practicum.explorewithme.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.explorewithme.dto.StatDTO;

import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleDataAccessException(final DataAccessException e) {
        log.warn("409", e);
        return ApiError.builder()
                .errors(List.of(Arrays.toString(e.getStackTrace())))
                .timestamp(LocalDateTime.now().format(StatDTO.formatDateTime))
                .message(e.getMessage())
                .reason("Data base access exception.")
                .status("CONFLICT")
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
        log.warn("400", e);
        return ApiError.builder()
                .errors(List.of(Arrays.toString(e.getStackTrace())))
                .timestamp(LocalDateTime.now().format(StatDTO.formatDateTime))
                .message(e.getMessage())
                .reason("Incorrectly made request.")
                .status("BAD_REQUEST")
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleConstraintViolationException(final ConstraintViolationException e) {
        log.warn("400", e);
        return ApiError.builder()
                .errors(List.of(Arrays.toString(e.getStackTrace())))
                .timestamp(LocalDateTime.now().format(StatDTO.formatDateTime))
                .message(e.getMessage())
                .reason("Incorrectly made request.")
                .status("BAD_REQUEST")
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleWrongEnum(final WrongEnumStatusException e) {
        log.warn(e.getMessage(), e);
        return ApiError.builder()
                .errors(List.of(Arrays.toString(e.getStackTrace())))
                .status(HttpStatus.valueOf(e.getCode()).toString())
                .timestamp(LocalDateTime.now().format(StatDTO.formatDateTime))
                .message(e.getMessage())
                .reason("Wrong enum member name")
                .build();
    }

    @ExceptionHandler
    public ApiError handleMissingParams(HttpServletResponse resp, MissingServletRequestParameterException e) {
        String name = e.getParameterName();
        log.warn(e.getMessage(), e);
        resp.setStatus(400);
        return ApiError.builder()
                .errors(List.of(Arrays.toString(e.getStackTrace())))
                .status(HttpStatus.valueOf(400).toString())
                .timestamp(LocalDateTime.now().format(StatDTO.formatDateTime))
                .message(e.getMessage())
                .reason("" + name + " parameter is missing")
                .build();
    }

    @ExceptionHandler
    public ApiError handleCustom(HttpServletResponse resp, final ApiErrorException e) {
        log.warn(e.getMessage(), e);
        resp.setStatus(e.getStatus());
        return ApiError.builder()
                .errors(List.of(Arrays.toString(e.getStackTrace())))
                .status(HttpStatus.valueOf(e.getStatus()).toString())
                .timestamp(LocalDateTime.now().format(StatDTO.formatDateTime))
                .message(e.getMessage())
                .reason(e.getReason())
                .build();
    }

    @ExceptionHandler
    public ApiError handleCustom(HttpServletResponse resp, final HttpCustomException e) {
        log.warn(e.getMessage(), e);
        resp.setStatus(e.getCode());
        return ApiError.builder()
                .errors(List.of(Arrays.toString(e.getStackTrace())))
                .status(HttpStatus.valueOf(e.getCode()).toString())
                .timestamp(LocalDateTime.now().format(StatDTO.formatDateTime))
                .message(e.getMessageWithoutCode())
                .reason(e.getMessage())
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleThrowable(HttpServletResponse resp, final Throwable e) {
        log.warn("500", e);
        return ApiError.builder()
                .errors(List.of(Arrays.toString(e.getStackTrace())))
                .status(HttpStatus.valueOf(resp.getStatus()).toString())
                .timestamp(LocalDateTime.now().format(StatDTO.formatDateTime))
                .message(e.getMessage())
                .reason("unknown")
                .build();
    }
}
