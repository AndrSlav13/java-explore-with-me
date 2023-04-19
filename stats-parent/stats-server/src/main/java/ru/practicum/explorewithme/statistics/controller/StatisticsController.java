package ru.practicum.explorewithme.statistics.controller;

import io.micrometer.core.lang.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.dto.StatDTO;
import ru.practicum.explorewithme.statistics.service.StatisticsService;

import javax.servlet.ServletRequest;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
public class StatisticsController {
    private final StatisticsService statisticsService;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public StatDTO.NewStatDTO addRecord(@RequestBody List<StatDTO.NewStatDTO> statDTO,
                                        @Nullable @RequestHeader("X-Explorer-User-Id") Long userId) {
        return statisticsService.addRecord(statDTO, userId);
    }

    @GetMapping(path = "/stats")
    public List<StatDTO.ReturnStatDTO> getRecords(@RequestParam String start,
                                                  @RequestParam String end,
                                                  @RequestParam(required = false) @Nullable List<String> uris,
                                                  @RequestParam(defaultValue = "false") String unique,
                                                  @Nullable @RequestHeader("X-Explorer-User-Id") Long userId,
                                                  @RequestParam(defaultValue = "0") Integer from,
                                                  @RequestParam(defaultValue = "10") Integer size,
                                                  ServletRequest servletRequest) {
        return statisticsService.getRecords(userId, start, end, uris, unique.toLowerCase(), from, size);
    }
}