package explore.with.me.statistics.client;

import explore.with.me.statistics.client.model.CreateEndpointHitDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Import;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Controller
@Validated
@RequiredArgsConstructor
@Import(MapperConfig.class)
public class StatisticsController {

    private final StatisticsClient statisticsClient;

    @PostMapping("/hit")
    public ResponseEntity<Object> createRecord(@RequestBody CreateEndpointHitDto dto) {
        log.info("There was request to the service '{}' by path '{}' from ip address {} at {}",
                dto.getApp(), dto.getUri(), dto.getIp(), dto.getTimestamp());
        return statisticsClient.createRecord(dto);
    }

    @GetMapping("/stats")
    public ResponseEntity<Object> getStatistics(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
            @RequestParam(required = false) List<String> uris,
            @RequestParam(required = false, defaultValue = "false") boolean unique) {
        log.info("Request to get statistics");

        if (start.isAfter(end)) {
            return ResponseEntity.badRequest().body("Start time should be after end time");
        }

        return statisticsClient.getRecords(start, end, uris, unique);
    }
}
