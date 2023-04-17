package ru.practicum.explorewithme.statistics.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.dto.StatDTO;
import ru.practicum.explorewithme.statistics.model.Statistics;
import ru.practicum.explorewithme.statistics.storage.StatisticsRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
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
    //Возвращается только первый DTO - он пришел на внешний endpoint. Остальные если есть, то в процессе его разворачивания
    public StatDTO.NewStatDTO addRecord(List<StatDTO.NewStatDTO> statDTO, Long userId) {
        List<Statistics> stat = statDTO.stream().map(a ->
                Statistics.builder()
                        .id(null)
                        .app(a.getApp())
                        .uri(a.getUri().toLowerCase())
                        .ip(a.getIp())
                        .timestamp(LocalDateTime.parse(a.getTimestamp(), StatDTO.formatDateTime))
                        .build()).collect(Collectors.toList());

        statisticsStore.saveAll(stat);
        return statDTO.get(0).toBuilder()
                .timestamp(stat.get(0).getTimestamp().format(StatDTO.formatDateTime))
                .id(stat.get(0).getId())
                .build();
    }

    @Override
    public List<StatDTO.ReturnStatDTO> getRecords(Long userId, String start, String end, List<String> urls, String unique, Integer from, Integer size) {
        boolean flag = Boolean.parseBoolean(unique);
        List<StatDTO.ReturnStatDTO> rezult = null;

        if (flag) rezult = statisticsStore.findStatisticsUniqueIp(
                urls,
                LocalDateTime.parse(start, StatDTO.formatDateTime),
                LocalDateTime.parse(end, StatDTO.formatDateTime)
        );
        else
            rezult = statisticsStore.findStatisticsNotUniqueIp(
                    urls,
                    LocalDateTime.parse(start, StatDTO.formatDateTime),
                    LocalDateTime.parse(end, StatDTO.formatDateTime)
            );


        Map<String, StatDTO.ReturnStatDTO> map = rezult.stream().collect(Collectors.toMap(a -> a.getUri(), a -> a, (a, b) -> a));
        if (!urls.isEmpty()) rezult = urls.stream().collect(Collectors.toMap(a -> a, a -> map.get(a) == null ?
                        StatDTO.ReturnStatDTO.builder().uri(a).hits(0L).build() :
                        map.get(a), (a, b) -> a))
                .values().stream().sorted((a, b) -> a.getHits() < b.getHits() ? 1 : -1).collect(Collectors.toList());
        return rezult;
    }
}
