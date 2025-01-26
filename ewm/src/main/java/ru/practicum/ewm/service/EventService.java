package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dal.CategoryRepository;
import ru.practicum.ewm.dal.EventRepository;
import ru.practicum.ewm.dal.UserRepository;
import ru.practicum.ewm.dto.event.EventFullDto;
import ru.practicum.ewm.dto.event.EventShortDto;
import ru.practicum.ewm.dto.event.NewEventDto;
import ru.practicum.ewm.dto.request.UpdateEventAdminRequest;
import ru.practicum.ewm.dto.request.UpdateEventUserRequest;
import ru.practicum.ewm.exception.ConditionException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.exception.ValidationException;
import ru.practicum.ewm.mapper.EventMapper;
import ru.practicum.ewm.model.AdminStateAction;
import ru.practicum.ewm.model.Category;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.EventStatus;
import ru.practicum.ewm.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.ewm.exception.ErrorMessages.CATEGORY_NOT_FOUND;
import static ru.practicum.ewm.exception.ErrorMessages.EVENT_NOT_FOUND;
import static ru.practicum.ewm.exception.ErrorMessages.USER_NOT_FOUND;
import static ru.practicum.ewm.model.EventStatus.PUBLISHED;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;

    private final UserRepository userRepository;

    private final CategoryRepository categoryRepository;

    private final EventMapper eventMapper;

    public List<EventFullDto> adminFindAll(List<Integer> users,
                                           List<EventStatus> states,
                                           List<Integer> categories,
                                           LocalDateTime rangeStart,
                                           LocalDateTime rangeEnd,
                                           int from,
                                           int size) {

        Pageable pageable = PageRequest.of(from / size, size);
        return eventRepository.findFilteredEvents(users, states, categories, rangeStart, rangeEnd, pageable).stream()
                .map(eventMapper::mapToFull)
                .toList();
    }

    public List<EventShortDto> findFilteredEvents(String text,
                                                  List<Integer> categories,
                                                  Boolean paid,
                                                  LocalDateTime rangeStart,
                                                  LocalDateTime rangeEnd,
                                                  boolean onlyAvailable,
                                                  String sort,
                                                  int from,
                                                  int size) {
        if (rangeStart == null) {
            rangeStart = LocalDateTime.now();
        }
        if (rangeEnd != null && rangeStart.isAfter(rangeEnd)) {
            throw new ConditionException("Start of events should be before end of events");
        }

        Pageable pageable = PageRequest.of(from / size, size);
        return eventMapper.mapToShort(eventRepository.findFilteredEvents(
                text, categories, paid, rangeStart,
                rangeEnd, onlyAvailable, sort, pageable).toList());
    }

    public List<EventShortDto> findByInitiator(int initiatorId, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        return eventRepository.findByInitiator_Id(initiatorId, pageable).stream()
                .map(eventMapper::mapToShort)
                .toList();
    }

    public EventFullDto findByInitiatorAndEvent(int initiatorId, int eventId) {
        Event event = eventRepository.findByInitiator_IdAndId(initiatorId, eventId)
                .orElseThrow(() -> new NotFoundException(EVENT_NOT_FOUND + eventId + " and userId: " + initiatorId));
        return eventMapper.mapToFull(event);
    }

    public EventFullDto findPublishedEventById(int eventId) {
        Event event = eventRepository.findByIdAndState(eventId, PUBLISHED)
                .orElseThrow(() -> new NotFoundException(EVENT_NOT_FOUND + eventId));
        return eventMapper.mapToFull(event);
    }

    public void updateViews(int eventId, int views) {
        Event event = eventRepository.findByIdAndState(eventId, PUBLISHED)
                .orElseThrow(() -> new NotFoundException(EVENT_NOT_FOUND + eventId));
        event.setViews(views);
        eventRepository.save(event);
    }

    public EventFullDto add(int initiatorId, NewEventDto dto) {
        checkEventTime(dto.getEventDate(), 2);

        User user = userRepository.findById(initiatorId)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND + initiatorId));
        Category category = categoryRepository.findById(dto.getCategory())
                .orElseThrow(() -> new NotFoundException(CATEGORY_NOT_FOUND + dto.getCategory()));

        Event toCreate = eventMapper.mapToFull(user, category, dto);
        Event created = eventRepository.save(toCreate);
        return eventMapper.mapToFull(created);
    }

    public EventFullDto userUpdate(int userId, int eventId, UpdateEventUserRequest request) {
        if (request.getAnnotation() != null && request.getAnnotation().isBlank()) {
            throw new ValidationException("Annotation can't be blank");
        }
        if (request.getDescription() != null && request.getDescription().isBlank()) {
            throw new ValidationException("Description can't be blank");
        }
        if (request.getTitle() != null && request.getTitle().isBlank()) {
            throw new ValidationException("Title can't be blank");
        }
        if (request.getEventDate() != null) {
            checkEventTime(request.getEventDate(), 2);
        }

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(EVENT_NOT_FOUND + eventId));

        if (event.getState() == PUBLISHED) {
            throw new ValidationException("Unable to update already published event " + eventId);
        }
        int categoryId = event.getCategory().getId();
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException(CATEGORY_NOT_FOUND + categoryId));

        Event toUpdate = eventMapper.updatedEvent(event, request, category);
        Event updated = eventRepository.save(toUpdate);
        return eventMapper.mapToFull(updated);
    }

    public EventFullDto adminUpdate(int eventId, UpdateEventAdminRequest request) {
        if (request.getAnnotation() != null && request.getAnnotation().isBlank()) {
            throw new ValidationException("Annotation can't be blank");
        }
        if (request.getDescription() != null && request.getDescription().isBlank()) {
            throw new ValidationException("Description can't be blank");
        }
        if (request.getTitle() != null && request.getTitle().isBlank()) {
            throw new ValidationException("Title can't be blank");
        }
        if (request.getEventDate() != null) {
            checkEventTime(request.getEventDate(), 1);
        }

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(EVENT_NOT_FOUND + eventId));
        if (request.getStateAction() != null && (
                (request.getStateAction().equals(AdminStateAction.PUBLISH_EVENT) && !event.getState().equals(EventStatus.PENDING)) ||
                        (request.getStateAction().equals(AdminStateAction.REJECT_EVENT) && event.getState().equals(PUBLISHED)))) {
            throw new ValidationException("Cannot publish the event because it's not in the right state: " + event.getState());
        }

        int categoryId = event.getCategory().getId();
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException(CATEGORY_NOT_FOUND + categoryId));

        Event toUpdate = eventMapper.updatedEvent(event, request, category);

        Event updated = eventRepository.save(toUpdate);
        return eventMapper.mapToFull(updated);
    }

    private void checkEventTime(LocalDateTime eventTime, int minHoursBeforeEvent) {
        if (eventTime.isBefore(LocalDateTime.now().plusHours(minHoursBeforeEvent))) {
            throw new ConditionException("Too late to create event, should be at least "
                    + minHoursBeforeEvent + " before event");
        }
    }
}
