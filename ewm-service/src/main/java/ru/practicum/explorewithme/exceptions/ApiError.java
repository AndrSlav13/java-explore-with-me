package ru.practicum.explorewithme.exceptions;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.explorewithme.dto.StatDTO;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class ApiError {
    private List<String> errors;
    private String message;
    private String reason;
    private String status;
    private String timestamp;

    public ApiError(HttpCustomException ex) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);

        errors.add(ex.toString());
        message = ex.getMessageWithoutCode();
        this.reason = ex.getMessage();
        status = ex.getMessage();
        timestamp = LocalDateTime.now().format(StatDTO.formatDateTime);
    }

    public ApiError(HttpCustomException ex, String reason) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);

        errors.add(ex.toString());
        message = ex.getMessageWithoutCode();
        this.reason = reason;
        this.status = ex.getMessage();
        timestamp = LocalDateTime.now().format(StatDTO.formatDateTime);
    }

    public ApiError(Throwable ex) {
        errors.add(Arrays.toString(ex.getStackTrace()));
        message = ex.getMessage();
        this.reason = ex.getMessage();
        status = ex.getMessage();
        timestamp = LocalDateTime.now().format(StatDTO.formatDateTime);
    }

    public ApiError(Throwable ex, String reason) {
        errors.add(Arrays.toString(ex.getStackTrace()));
        message = ex.getMessage();
        this.reason = reason;
        status = ex.getMessage();
        timestamp = LocalDateTime.now().format(StatDTO.formatDateTime);
    }
}
