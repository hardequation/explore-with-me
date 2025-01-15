package explore.with.me.statistics.server.controller;

import explore.with.me.statistics.server.model.CreateEndpointHitDto;
import explore.with.me.statistics.server.model.EndpointHit;
import explore.with.me.statistics.server.model.ViewStats;
import explore.with.me.statistics.server.service.StatsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class StatsController {

    private final StatsService statsService;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<EndpointHit> saveHit(@RequestBody CreateEndpointHitDto hit) {
        log.info("Hit saved: " + hit.getUri());
        return ResponseEntity.status(HttpStatus.CREATED).body(statsService.saveHit(hit));
    }


    @GetMapping("/stats")
    public List<ViewStats> getStats(
            @RequestParam LocalDateTime start,
            @RequestParam LocalDateTime end,
            @RequestParam(required = false) List<String> uris,
            @RequestParam(required = false, defaultValue = "false") boolean unique) {

        log.info("Fetching stats from {} to {}, URIs: {}, Unique: {}", start, end, uris, unique);
        return statsService.getStats(start, end, uris, unique);
    }
}

