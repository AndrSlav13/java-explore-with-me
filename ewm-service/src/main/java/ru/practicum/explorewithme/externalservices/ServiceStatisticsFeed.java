package ru.practicum.explorewithme.externalservices;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import ru.practicum.explorewithme.client.StatisticsClient;
import ru.practicum.explorewithme.dto.StatDTO;
import ru.practicum.explorewithme.event.dto.EventDTO;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Перехват вызовов функций и отправка данных в сервис статистики
 */

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class ServiceStatisticsFeed {
    private final StatisticsClient statisticsClient;

    @AfterReturning("execution(* ru.practicum.explorewithme.event.controller.EventController.getEventByEventIdPublic(..)) && args(servletRequest, id))")
    public void afterReturningCallgetEventByEventIdPublic(HttpServletRequest servletRequest, Long id) {
        log.info("ru.practicum.explorewithme.event.controller.EventController.getEventByEventIdPublic(..) id={} ", servletRequest, id);
        postStatistics(servletRequest);
    }

    @AfterReturning(pointcut = "execution(* ru.practicum.explorewithme.event.controller.EventController.getEventsPublic(..)) && args(servletRequest,..))", returning = "retVal")
    public void afterReturningCallgetEventsPublic(HttpServletRequest servletRequest, List<EventDTO.Controller.EventShortDto> retVal) {
        log.info("ru.practicum.explorewithme.event.controller.EventController.getEventsPublic(..)");
        postStatistics(servletRequest);
    }

    private static final String[] VALID_IP_HEADER_CANDIDATES = {
            "X-Forwarded-For",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_X_FORWARDED_FOR",
            "HTTP_X_FORWARDED",
            "HTTP_X_CLUSTER_CLIENT_IP",
            "HTTP_CLIENT_IP",
            "HTTP_FORWARDED_FOR",
            "HTTP_FORWARDED",
            "HTTP_VIA",
            "REMOTE_ADDR"};

    public static String getClientIpAddress(HttpServletRequest request) {
        for (String header : VALID_IP_HEADER_CANDIDATES) {
            String ipAddress = request.getHeader(header);
            if (ipAddress != null && ipAddress.length() != 0 && !"unknown".equalsIgnoreCase(ipAddress)) {
                return ipAddress;
            }
        }
        return request.getRemoteAddr();
    }

    public void postStatistics(HttpServletRequest servletRequest) {
        StatDTO.NewStatDTO requestDto = StatDTO.NewStatDTO.builder()
                .id(null)
                .timestamp(LocalDateTime.now().format(StatDTO.formatDateTime))
                .app("ewm-service")
                .ip(getClientIpAddress(servletRequest))
                .uri(servletRequest.getRequestURI())
                .build();
        statisticsClient.addRecords(0L, List.of(requestDto));
    }

    public void postStatistics(HttpServletRequest servletRequest, List<EventDTO.Controller.EventShortDto> retVal) {
        List<StatDTO.NewStatDTO> requestDto = retVal.stream().map(a -> StatDTO.NewStatDTO.builder()
                .id(null)
                .timestamp(LocalDateTime.now().format(StatDTO.formatDateTime))
                .app("ewm-service")
                .ip(getClientIpAddress(servletRequest))
                .uri(servletRequest.getRequestURI() + "/" + a.getId())
                .build()).collect(Collectors.toList());
        statisticsClient.addRecords(0L, requestDto);
    }
}
