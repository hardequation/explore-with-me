package ru.practicum.ewm.controller.forall;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.dto.compilation.CompilationDto;
import ru.practicum.ewm.model.StatEvent;
import ru.practicum.ewm.service.CompilationService;
import ru.practicum.ewm.service.StatisticsService;

import java.util.List;

import static ru.practicum.ewm.utils.Constants.MAIN_SERVICE;

@RestController
@RequestMapping(path = "/compilations")
@RequiredArgsConstructor
public class PublicCompilationController {

    private final CompilationService compilationService;

    private final StatisticsService statService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CompilationDto> get(@RequestParam(required = false) boolean pinned,
                                    @RequestParam(defaultValue = "0") int from,
                                    @RequestParam(defaultValue = "10") int size,
                                    HttpServletRequest request) {
        StatEvent statEvent = StatEvent.builder()
                .serviceName(MAIN_SERVICE)
                .uri(request.getRequestURI())
                .build();
        statService.sendStat(statEvent, request);
        return compilationService.findAll(pinned, from, size);
    }

    @GetMapping("/{compId}")
    @ResponseStatus(HttpStatus.OK)
    public CompilationDto get(@PathVariable int compId, HttpServletRequest request) {
        StatEvent statEvent = StatEvent.builder()
                .serviceName(MAIN_SERVICE)
                .uri(request.getRequestURI())
                .build();
        statService.sendStat(statEvent, request);
        return compilationService.findById(compId);
    }


}
