package ru.practicum.explorewithme.statistics.service;

import org.springframework.http.HttpStatus;
import ru.practicum.explorewithme.dto.StatDTO;
import ru.practicum.explorewithme.exceptions.HttpCustomException;
import ru.practicum.explorewithme.statistics.model.Statistics;

import java.util.List;

public interface StatisticsService extends StatisticsServiceSort {
    StatDTO.NewStatDTO addRecord(List<StatDTO.NewStatDTO> statDTO, Long userId);

    List<StatDTO.ReturnStatDTO> getRecords(Long userId, String start, String end, List<String> urls, String unique, Integer from, Integer size);

    static boolean validate(Statistics statistics) {
        if (statistics == null) throw new HttpCustomException(HttpStatus.NOT_FOUND, "Wrong booking id");
        return true;
    }
}
