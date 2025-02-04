package ru.practicum.ewm.controller.foruser;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.dto.event.EventShortDto;
import ru.practicum.ewm.dto.user.UserShortDto;
import ru.practicum.ewm.service.RecommendationService;

import java.util.List;

@RestController
@RequestMapping(path = "/users/recommendations")
@RequiredArgsConstructor
public class PrivateRecommendationController {

    private final RecommendationService recommendationService;

    @GetMapping("/{userId}/user")
    public List<UserShortDto> getUserRecommendations(@PathVariable int userId) {
        return recommendationService.getUserRecommendations(userId);
    }

    @GetMapping("/{userId}/events")
    public List<EventShortDto> getEventRecommendations(@PathVariable int userId) {
        return recommendationService.getEventRecommendations(userId);
    }
}
