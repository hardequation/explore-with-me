package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dal.EventRepository;
import ru.practicum.ewm.dal.RequestRepository;
import ru.practicum.ewm.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.ewm.dto.request.ParticipationRequestDto;
import ru.practicum.ewm.exception.ApproveRequestException;
import ru.practicum.ewm.exception.AuthentificationException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.exception.ValidationException;
import ru.practicum.ewm.mapper.ParticipationRequestMapper;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.EventStatus;
import ru.practicum.ewm.model.ParticipationRequest;
import ru.practicum.ewm.model.RequestStatus;

import java.util.ArrayList;
import java.util.List;

import static ru.practicum.ewm.exception.ErrorMessages.EVENT_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class RequestService {

    private final RequestRepository requestRepository;

    private final EventRepository eventRepository;

    private final ParticipationRequestMapper requestMapper;

    public List<ParticipationRequestDto> getRequestsByUserId(int userId) {
        return requestRepository.findByRequesterId(userId)
                .stream()
                .map(requestMapper::map)
                .toList();
    }

    public ParticipationRequestDto createParticipationRequest(int userId, int eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(EVENT_NOT_FOUND + eventId));
        if (event.getInitiator().getId().equals(userId)) {
            throw new ValidationException("Initiator of event can't send participation request");
        }
        if (!event.getState().equals(EventStatus.PUBLISHED)) {
            throw new ValidationException("Cannot join an unpublished event.");
        }
        if (!requestRepository.findByRequesterIdAndEventId(userId, eventId).isEmpty()) {
            throw new ValidationException("Duplicate request is not allowed.");
        }
        if (event.getParticipantLimit() > 0 && event.getConfirmedRequests() >= event.getParticipantLimit()) {
            throw new ApproveRequestException("Event participation limit reached.");
        }

        RequestStatus status;
        if (!event.isRequestModeration()) {
            status = RequestStatus.CONFIRMED;
        } else {
            if (event.getParticipantLimit() == 0) {
                status = RequestStatus.CONFIRMED;
            } else {
                status = RequestStatus.PENDING;
            }
        }

        if (status.equals(RequestStatus.CONFIRMED)) {
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
        }

        ParticipationRequest request = ParticipationRequest.builder()
                .requesterId(userId)
                .eventId(eventId)
                .status(status)
                .build();

        ParticipationRequest savedRequest = requestRepository.save(request);
        return requestMapper.map(savedRequest);
    }

    public ParticipationRequestDto cancelRequest(int userId, int requestId) {
        ParticipationRequest request = requestRepository.findByIdAndRequesterId(requestId, userId)
                .orElseThrow(() -> new ValidationException("Request not found"));

        if (request.getRequesterId() != userId) {
            throw new AuthentificationException("Only requester can cancel its request");
        }

        request.setStatus(RequestStatus.CANCELED);
        ParticipationRequest updatedRequest = requestRepository.save(request);

        return requestMapper.map(updatedRequest);
    }

    public List<ParticipationRequestDto> getRequestsInfo(int userId, int eventId) {
        List<ParticipationRequest> requests = requestRepository.findByInitiatorIdAndEventId(userId, eventId);
        return requests.stream()
                .map(requestMapper::map)
                .toList();
    }

    public EventRequestStatusUpdateResult updateRequestStatus(int initiatorId, int eventId, EventRequestStatusUpdateRequest request) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(EVENT_NOT_FOUND + eventId));

        if (initiatorId != event.getInitiator().getId()) {
            throw new AuthentificationException("Only initiator of event can reply to the request");
        }

        if (event.getParticipantLimit() == event.getConfirmedRequests()) {
            throw new ApproveRequestException(String.format("The participant limit has been reached for event %s", eventId));
        }

        if (event.getParticipantLimit() == 0 || !event.isRequestModeration()) {
            return new EventRequestStatusUpdateResult();
        }

        List<ParticipationRequest> reqs = requestRepository.findByIdIn(request.getRequestIds());

        if (request.getStatus().equals(RequestStatus.REJECTED)) {
            return new EventRequestStatusUpdateResult(
                    new ArrayList<>(),
                    reqs.stream().peek(r -> r.setStatus(RequestStatus.REJECTED)).map(requestMapper::map).toList());
        }

        // If available places >= toConfirm => confirm all toConfirm requests
        // If available places < toConfirm => confirm all available requests
        int availableRequestsToConfirm = Math.min(event.getParticipantLimit() - event.getConfirmedRequests(), reqs.size());
        event.setConfirmedRequests(event.getConfirmedRequests() + availableRequestsToConfirm);
        eventRepository.save(event);

        EventRequestStatusUpdateResult result = new EventRequestStatusUpdateResult(new ArrayList<>(), new ArrayList<>());
        int i = 0;
        while (availableRequestsToConfirm - i > 0) {
            ParticipationRequest req = reqs.get(i);
            if (!req.getStatus().equals(RequestStatus.PENDING)) {
                throw new ApproveRequestException(String.format("Request %s must have status PENDING", req.getId()));
            }

            req.setStatus(RequestStatus.CONFIRMED);
            result.getConfirmedRequests().add(requestMapper.map(req));
            requestRepository.save(req);
            i++;
        }

        for (int j = i; j < reqs.size(); j++) {
            ParticipationRequest req = reqs.get(j);
            req.setStatus(RequestStatus.REJECTED);
            result.getRejectedRequests().add(requestMapper.map(req));
            requestRepository.save(req);
        }

        return result;
    }
}
