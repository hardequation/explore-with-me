package ru.practicum.ewm.controller.forall;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.dto.event.EventFullDto;
import ru.practicum.ewm.dto.event.EventShortDto;
import ru.practicum.ewm.model.StatEvent;
import ru.practicum.ewm.service.EventService;
import ru.practicum.ewm.service.StatisticsService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static ru.practicum.ewm.utils.Constants.DATE_TIME_FORMAT;
import static ru.practicum.ewm.utils.Constants.MAIN_SERVICE;

@RestController
@RequestMapping(path = "/events")
@RequiredArgsConstructor
public class PublicEventController {

    private final EventService eventService;

    private final StatisticsService statService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> get(
            @RequestParam(required = false) String text,
            @RequestParam(required = false) List<Integer> categories,
            @RequestParam(required = false) Boolean paid,
            @RequestParam(required = false) @DateTimeFormat(pattern = DATE_TIME_FORMAT) LocalDateTime rangeStart,
            @RequestParam(required = false) @DateTimeFormat(pattern = DATE_TIME_FORMAT) LocalDateTime rangeEnd,
            @RequestParam(defaultValue = "false") Boolean onlyAvailable,
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {
        StatEvent statEvent = StatEvent.builder()
                .serviceName(MAIN_SERVICE)
                .uri(request.getRequestURI())
                .build();
        statService.sendStat(statEvent, request);
        return eventService.findFilteredEvents(
                text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto getEventById(@PathVariable int id, HttpServletRequest request) {
        StatEvent statEvent = StatEvent.builder()
                .serviceName(MAIN_SERVICE)
                .uri(request.getRequestURI())
                .build();
        statService.sendStat(statEvent, request);
        int views = statService.getStats(null, null,
                Collections.singletonList(request.getRequestURI()), true).size();
        return eventService.findPublishedEventById(id, views);
    }
}
