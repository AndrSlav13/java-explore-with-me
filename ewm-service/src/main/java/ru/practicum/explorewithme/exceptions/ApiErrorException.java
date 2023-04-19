package ru.practicum.explorewithme.exceptions;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import ru.practicum.explorewithme.dto.StatDTO;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Getter
public class ApiErrorException extends RuntimeException {
    private int status;
    private String message;
    private String reason;
    private LocalDateTime time;

    public ApiErrorException(HttpStatus status, String message, String reason) {
        this.status = status.value();
        this.message = message;
        this.reason = reason;
        time = LocalDateTime.now();
        log.info("code: " + status + " : " + message);
    }

    public ApiErrorException(Integer code, String message, String reason) {
        this(HttpStatus.valueOf(code), message, reason);
    }

    public ApiError getApiError() {
        return ApiError.builder()
                .errors(List.of(Arrays.toString(this.getStackTrace())))
                .timestamp(this.time.format(StatDTO.formatDateTime))
                .message(this.message)
                .reason(this.reason)
                .build();
    }
}
