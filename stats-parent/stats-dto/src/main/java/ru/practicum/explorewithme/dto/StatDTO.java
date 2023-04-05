package ru.practicum.explorewithme.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.explorewithme.model.ServiceIDConstrain;

import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public enum StatDTO {
    ;
    private static String formatDateTimeS = "yyyy-MM-dd HH:mm:ss";
    public static final DateTimeFormatter formatDateTime = DateTimeFormatter.ofPattern(formatDateTimeS);
    public static final String patternIp = "^[0-9]{1,4}\\.[0-9]{1,4}\\.[0-9]{1,4}\\.[0-9]{1,4}$";
    public static final String patternTimeStamp = "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}$";
    public static final List<String> serviceIDs = List.of("ewm-main-service");

    public static LocalDateTime stringToLocalDateTime(String str) {
        LocalDateTime dateTime = LocalDateTime.parse(str, formatDateTime);
        return dateTime;
    }

    private interface Ip {
        @Pattern(regexp = patternIp)
        public String getIp();
    }

    private interface Timestamp {
        @Pattern(regexp = patternTimeStamp)
        public String getTimestamp();
    }

    private interface App {
        @ServiceIDConstrain
        public String getApp();
    }

    @Builder(toBuilder = true)
    @Data
    public static class NewStatDTO implements Ip, Timestamp, App {
        Long id;
        String app;     //application/module to get statistics
        String uri;     //uri of interest
        String ip;      //ip of user
        String timestamp;   //
    }

    @Builder
    @Data
    @AllArgsConstructor
    public static class ReturnStatDTO {
        String app;
        String uri;
        Long hits;   //number of appeals
    }
}