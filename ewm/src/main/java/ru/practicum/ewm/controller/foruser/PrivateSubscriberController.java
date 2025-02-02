package ru.practicum.ewm.controller.foruser;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.dto.subscription.SubscriptionDto;
import ru.practicum.ewm.dto.user.UserShortDto;
import ru.practicum.ewm.service.SubscriptionService;

import java.util.List;

@RestController
@RequestMapping(path = "/users/subscriptions")
@RequiredArgsConstructor
public class PrivateSubscriberController {

    private final SubscriptionService subscriptionService;

    @GetMapping("/{userId}/{subscriberId}")
    public boolean exists(@PathVariable int userId,
                          @PathVariable int subscriberId) {
        return subscriptionService.exists(userId, subscriberId);
    }

    @GetMapping("/{userId}")
    public List<UserShortDto> getSubscribers(@PathVariable int userId) {
        return subscriptionService.findSubscribers(userId);
    }

    @PostMapping("/{userId}/{subscriberId}")
    public SubscriptionDto subscribe(@PathVariable int userId,
                                     @PathVariable int subscriberId) {
        return subscriptionService.subscribe(userId, subscriberId);
    }

    @DeleteMapping("/{userId}/{subscriberId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unsubscribe(@PathVariable int userId, @PathVariable int subscriberId) {
        subscriptionService.unsubscribe(userId, subscriberId);
    }
}
