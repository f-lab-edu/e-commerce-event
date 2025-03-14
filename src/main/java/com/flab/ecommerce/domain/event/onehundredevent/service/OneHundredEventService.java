package com.flab.ecommerce.domain.event.onehundredevent.service;

import com.flab.ecommerce.domain.event.entity.Event;
import com.flab.ecommerce.domain.event.enums.EventType;
import com.flab.ecommerce.domain.event.exception.EventNotFoundException;
import com.flab.ecommerce.domain.event.exception.EventStockNotEnoughException;
import com.flab.ecommerce.domain.event.exception.InvalidEventTypeException;
import com.flab.ecommerce.domain.event.onehundredevent.entity.OneHundredEventParticipant;
import com.flab.ecommerce.domain.event.onehundredevent.enums.ParticipantStatus;
import com.flab.ecommerce.domain.event.onehundredevent.exception.OneHundredEventException;
import com.flab.ecommerce.domain.event.onehundredevent.repository.OneHundredEventRepository;
import com.flab.ecommerce.domain.event.policy.EventPolicy;
import com.flab.ecommerce.domain.event.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class OneHundredEventService {


    private final EventRepository eventRepository;
    private final OneHundredEventRepository oneHundredEventRepository;
    private final OneHundredEventQueueService oneHundredEventQueueService;
    private final EventPolicy eventPolicy;

    @Transactional
    public void addToQueue(Long eventId, Long memberId) {
        validateEvent(eventId);
        validateQueue(eventId, memberId);
        oneHundredEventQueueService.addToQueue(eventId, memberId);
    }

    @Transactional
    public void confirmParticipation(Long eventId, Long memberId) {
        OneHundredEventParticipant participant = validateParticipant(eventId, memberId);
        Event event = validateEvent(eventId);
        validateStock(event);

        participant.confirmParticipation();
        event.decreaseEventStock();
    }

    private Event validateEvent(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(EventNotFoundException::new);

        if (event.getType() != EventType.ONE_HUNDRED_WON_DEAL) {
            throw new InvalidEventTypeException();
        }
        return event;
    }

    private void validateQueue(Long eventId, Long memberId) {
        if (oneHundredEventQueueService.isUserInQueue(eventId, memberId)) {
            throw new OneHundredEventException.AlreadyInQueueException();
        }

        if (oneHundredEventRepository.existsByEventIdAndMemberId(eventId, memberId)) {
            throw new OneHundredEventException.AlreadyParticipatedException();
        }
    }

    private OneHundredEventParticipant validateParticipant(Long eventId, Long memberId) {
        OneHundredEventParticipant participant = oneHundredEventRepository
                .findByEventIdAndMemberId(eventId, memberId)
                .orElseThrow(OneHundredEventException.NotInPendingStateException::new);

        if (participant.getStatus() != ParticipantStatus.PENDING) {
            throw new OneHundredEventException.NotInPendingStateException();
        }
        return participant;
    }

    private void validateStock(Event event) {
        if (event.getEventStock() <= 0) {
            throw new EventStockNotEnoughException();
        }
    }
}
