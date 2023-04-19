package ru.practicum.explorewithme.controller;

import io.micrometer.core.lang.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> addRecord(@RequestBody @Valid StatDTO.NewStatDTO statDTO,
                                            @Nullable @RequestHeader("X-Explorer-User-Id") Long userId) {
        return statisticsClient.addRecords(userId, List.of(statDTO));
    }

    @GetMapping(path = "/stats")
    public ResponseEntity<Object> getRecords(@Pattern(regexp = StatDTO.patternTimeStamp, message = "wrong format for time interval 'start'") @RequestParam String start,
                                             @Pattern(regexp = StatDTO.patternTimeStamp, message = "wrong format for time interval 'end'") @RequestParam String end,
                                             @RequestParam(required = false) @Nullable List<String> uris,
                                             @RequestParam(defaultValue = "false") Boolean unique,
                                             @Nullable @RequestHeader("X-Explorer-User-Id") Long userId,
                                             @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                             @Positive @RequestParam(defaultValue = "10") Integer size) {
        return statisticsClient.getRecords(userId,
                StatDTO.stringToLocalDateTime(start),
                StatDTO.stringToLocalDateTime(end),
                uris,
                unique,
                from,
                size);
    }
}