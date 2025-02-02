package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dal.SubscriptionRepository;
import ru.practicum.ewm.dal.UserRepository;
import ru.practicum.ewm.dto.subscription.SubscriptionDto;
import ru.practicum.ewm.dto.user.UserShortDto;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.exception.ValidationException;
import ru.practicum.ewm.mapper.SubscriptionMapper;
import ru.practicum.ewm.mapper.UserMapper;
import ru.practicum.ewm.model.Subscription;
import ru.practicum.ewm.model.SubscriptionId;
import ru.practicum.ewm.model.User;

import java.util.List;

import static ru.practicum.ewm.exception.ErrorMessages.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final UserRepository userRepository;

    private final SubscriptionRepository subscriptionRepository;

    private final UserMapper userMapper;

    private final SubscriptionMapper subscriptionMapper;

    public List<UserShortDto> findSubscribers(int userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(USER_NOT_FOUND + userId);
        }
        return subscriptionRepository.findSubscribers(userId).stream()
                .map(userMapper::map)
                .toList();

    }

    public SubscriptionDto subscribe(int userId, int subscriberId) {
        if (!userRepository.existsById(userId) || !userRepository.existsById(subscriberId)) {
            throw new NotFoundException("Users " + userId + " and " + subscriberId + " should exist");
        }

        SubscriptionId id = SubscriptionId.builder()
                .userId(userId)
                .subscriberId(subscriberId)
                .build();

        if (subscriptionRepository.existsById(id)) {
            throw new ValidationException("Subscription already exists");
        }
        checkSubscribersGroup(userId, subscriberId);

        Subscription request = Subscription.builder()
                .id(id)
                .build();
        return subscriptionMapper.map(subscriptionRepository.save(request));
    }

    public boolean exists(int userId, int subscriberId) {
        SubscriptionId id = SubscriptionId.builder()
                .userId(userId)
                .subscriberId(subscriberId)
                .build();

        return subscriptionRepository.existsById(id);
    }

    public void unsubscribe(int userId, int subscriberId) {
        if (!userRepository.existsById(userId) || !userRepository.existsById(subscriberId)) {
            throw new NotFoundException(
                    String.format("User %s and subscriber %s should exist in database", userId, subscriberId));
        }

        SubscriptionId id = SubscriptionId.builder()
                .userId(userId)
                .subscriberId(subscriberId)
                .build();

        if (!subscriptionRepository.existsById(id)) {
            throw new NotFoundException("Subscription doesn't exist");
        }
        subscriptionRepository.deleteById(id);
    }

    private void checkSubscribersGroup(int userId, int subscriberId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(USER_NOT_FOUND + userId));

        switch (user.getSubscriberGroup()) {
            case NOBODY -> throw new ValidationException("Nobody can subscribe on user " + userId);

            case SUBSCRIBER_OF_SUBSCRIBERS -> {
                if (!subscriptionRepository.isSubscriberOfSubscribers(userId, subscriberId)) {
                    throw new ValidationException("Only subscribers of subscribers can subscribe on user " + userId);
                }
            }
        }
    }
}
