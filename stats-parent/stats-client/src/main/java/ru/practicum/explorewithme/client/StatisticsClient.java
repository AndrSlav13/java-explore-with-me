package ru.practicum.explorewithme.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.explorewithme.dto.StatDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class StatisticsClient extends BaseClient {

    @Autowired
    public StatisticsClient(@Value("${explore-with-me-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + ""))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> getRecords(Long userId, LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique, Integer from, Integer size) {
        Object urisList = uris != null ? uris.toArray() : "";
        Map<String, Object> parameters = Map.of(
                "start", start.format(StatDTO.formatDateTime),
                "end", end.format(StatDTO.formatDateTime),
                "from", from,
                "size", size,
                "unique", unique,
                "uris", urisList);

        return get("/stats?start={start}&end={end}&unique={unique}&from={from}&size={size}&uris={uris}", userId, parameters);
    }

    public ResponseEntity<Object> addRecords(Long userId, List<StatDTO.NewStatDTO> requestDto) {
        return post("/hit", userId, requestDto);
    }
}