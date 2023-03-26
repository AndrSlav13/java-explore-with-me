package ru.practicum.explorewithme.controller;

import io.micrometer.core.lang.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.client.StatisticsClient;
import ru.practicum.explorewithme.dto.StatDTO;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
public class StatisticsController {
    private final StatisticsClient statisticsClient;

    @PostMapping("/hit")
    public ResponseEntity<Object> addRecord(@RequestBody @Valid StatDTO.NewStatDTO statDTO,
                                            @Nullable @RequestHeader("X-Explorer-User-Id") Long userId) {
        throw new RuntimeException("something");
        //return statisticsClient.addRecord(userId, statDTO);
    }

    @GetMapping(path = "/stats")
    public ResponseEntity<Object> getRecords(@Pattern(regexp = StatDTO.patternTimeStamp, message = "wrong format for time interval 'start'") @RequestParam String start,
                                             @Pattern(regexp = StatDTO.patternTimeStamp, message = "wrong format for time interval 'end'") @RequestParam String end,
                                             @RequestParam List<String> uris,
                                             @Pattern(regexp = "(?i)^trUe$|^faLse$", message = "wrong format for check uniquness of users") @RequestParam(defaultValue = "false") String unique,
                                             @Nullable @RequestHeader("X-Explorer-User-Id") Long userId,
                                             @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                             @Positive @RequestParam(defaultValue = "10") Integer size) {
        return statisticsClient.getRecords(userId,
                StatDTO.stringToLocalDateTime(start),
                StatDTO.stringToLocalDateTime(end),
                uris,
                Boolean.parseBoolean(unique.toLowerCase()),
                from,
                size);
    }
}