package ru.practicum.explorewithme.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

@Slf4j
public class HttpCustomException extends RuntimeException {
    private int code;
    private String message;

    public HttpCustomException(HttpStatus code, String message) {
        this.code = code.value();
        this.message = message;
        log.info("code: " + code + " : " + message);
    }

    public HttpCustomException(HttpStatus code) {
        this.code = code.value();
        this.message = "Unknown net error";
        log.info(getMessage());
    }

    public HttpCustomException(String message) {
        this(HttpStatus.NOT_ACCEPTABLE, message);
    }

    public HttpCustomException() {
        this.code = 400;
        this.message = "Unknown net error";
        log.info(this.getStackTrace().toString());
    }

    public int getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return "" + code + " : " + message;
    }

    public String getMessageWithoutCode() {
        return message;
    }

    public void setMessage(String s) {
        message = s;
    }
}
