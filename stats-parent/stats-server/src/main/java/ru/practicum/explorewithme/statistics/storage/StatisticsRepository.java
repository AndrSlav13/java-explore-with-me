package ru.practicum.explorewithme.statistics.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.explorewithme.dto.StatDTO;
import ru.practicum.explorewithme.statistics.model.Statistics;

import java.time.LocalDateTime;
import java.util.List;

public interface StatisticsRepository extends JpaRepository<Statistics, Long> {
    @Query("select new ru.practicum.explorewithme.dto.StatDTO$ReturnStatDTO(s.app, s.uri, count(s.ip)) from Statistics as s " +
            "where lower(:urls) like '%' || lower(s.uri) || '%' " +
            "and s.timestamp > :timeStart " +
            "and s.timestamp < :timeEnd " +
            "group by s.uri, s.app " +
            "order by count(s.ip) desc ")
    List<StatDTO.ReturnStatDTO> findStatisticsNotUniqueIp(@Param("urls") String urls, @Param("timeStart") LocalDateTime start, @Param("timeEnd") LocalDateTime end);

    @Query("select new ru.practicum.explorewithme.dto.StatDTO$ReturnStatDTO(s.app, s.uri, count(distinct s.ip)) from Statistics as s " +
            "where lower(:urls) like '%' || lower(s.uri) || '%' " +
            "and s.timestamp > :timeStart " +
            "and s.timestamp < :timeEnd " +
            "group by s.uri, s.app " +
            "order by count(s.ip) desc ")
    List<StatDTO.ReturnStatDTO> findStatisticsUniqueIp(@Param("urls") String urls, @Param("timeStart") LocalDateTime start, @Param("timeEnd") LocalDateTime end);
}
