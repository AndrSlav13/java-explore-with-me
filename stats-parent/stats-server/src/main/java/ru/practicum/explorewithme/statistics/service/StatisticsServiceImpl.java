package ru.practicum.explorewithme.statistics.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.dto.StatDTO;
import ru.practicum.explorewithme.statistics.model.Statistics;
import ru.practicum.explorewithme.statistics.storage.StatisticsRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class StatisticsServiceImpl implements StatisticsService {

    private final StatisticsRepository statisticsStore;

    @Autowired
    public StatisticsServiceImpl(StatisticsRepository statisticsStore) {
        this.statisticsStore = statisticsStore;
    }

    @Transactional
    @Override
    public StatDTO.NewStatDTO addRecord(StatDTO.NewStatDTO statDTO, Long userId) {
        Statistics stat = Statistics.builder()
                .id(null)
                .app(statDTO.getApp())
                .uri(statDTO.getUri())
                .ip(statDTO.getIp())
                .timestamp(LocalDateTime.parse(statDTO.getTimestamp(), StatDTO.formatDateTime))
                .build();

        statisticsStore.save(stat);
        return statDTO.toBuilder()
                .timestamp(stat.getTimestamp().format(StatDTO.formatDateTime))
                .id(stat.getId())
                .build();
    }

    @Override
    public List<StatDTO.ReturnStatDTO> getRecords(Long userId, String start, String end, List<String> urls, String unique, Integer from, Integer size) {
        boolean flag = Boolean.parseBoolean(unique);
        if(flag) return statisticsStore.findStatisticsUniqueIp(
                urls.stream().collect(Collectors.joining("%")),
                LocalDateTime.parse(start, StatDTO.formatDateTime),
                LocalDateTime.parse(end, StatDTO.formatDateTime)
        );

        return statisticsStore.findStatisticsNotUniqueIp(
                urls.stream().collect(Collectors.joining("%")),
                LocalDateTime.parse(start, StatDTO.formatDateTime),
                LocalDateTime.parse(end, StatDTO.formatDateTime)
        );
    }
}
