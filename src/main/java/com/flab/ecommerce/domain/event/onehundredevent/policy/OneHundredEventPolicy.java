package com.flab.ecommerce.domain.event.onehundredevent.policy;

import com.flab.ecommerce.domain.event.entity.Event;
import com.flab.ecommerce.domain.event.exception.EventNotFoundException;
import com.flab.ecommerce.domain.event.exception.EventStockNotEnoughException;
import com.flab.ecommerce.domain.event.exception.MaxParticipantsExceededException;
import com.flab.ecommerce.domain.event.onehundredevent.entity.OneHundredEventParticipant;
import com.flab.ecommerce.domain.event.onehundredevent.enums.ParticipantStatus;
import com.flab.ecommerce.domain.event.onehundredevent.repository.OneHundredEventRepository;
import com.flab.ecommerce.domain.event.policy.EventPolicy;
import com.flab.ecommerce.domain.event.queue.EventQueue;
import com.flab.ecommerce.domain.event.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class OneHundredEventPolicy extends EventPolicy {

    private final EventQueue eventQueue;
    private final OneHundredEventRepository participantRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public void applyEvent(Long eventId, Long memberId) {

        Event event = eventRepository.findById(eventId)
                .orElseThrow(EventNotFoundException::new);

        long currentParticipants = participantRepository.countByEventId(eventId);
        if (currentParticipants >= event.getMaxParticipants()) {
            throw new MaxParticipantsExceededException();
        }

        if (event.getEventStock() <= 0) {
            throw new EventStockNotEnoughException();
        }

        OneHundredEventParticipant participant = OneHundredEventParticipant.builder()
                .eventId(eventId)
                .memberId(memberId)
                .status(ParticipantStatus.PENDING)
                .expiredAt(LocalDateTime.now().plusMinutes(5))
                .build();

        participantRepository.save(participant);
        event.decreaseEventStock();

    }
}

