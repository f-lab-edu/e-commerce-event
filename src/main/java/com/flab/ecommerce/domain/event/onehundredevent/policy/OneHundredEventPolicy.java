package com.flab.ecommerce.domain.event.onehundredevent.policy;

import com.flab.ecommerce.domain.event.entity.Event;
import com.flab.ecommerce.domain.event.exception.EventNotFoundException;
import com.flab.ecommerce.domain.event.exception.MaxParticipantsExceededException;
import com.flab.ecommerce.domain.event.onehundredevent.counter.OneHundredEventCounter;
import com.flab.ecommerce.domain.event.onehundredevent.entity.OneHundredEventParticipant;
import com.flab.ecommerce.domain.event.onehundredevent.enums.ParticipantStatus;
import com.flab.ecommerce.domain.event.onehundredevent.repository.OneHundredEventRepository;
import com.flab.ecommerce.domain.event.policy.EventPolicy;
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

    private final OneHundredEventRepository participantRepository;
    private final EventRepository eventRepository;
    private final OneHundredEventCounter participantCounter;


    @Override
    @Transactional
    public void applyEvent(Long eventId, Long memberId) {

        Event event = eventRepository.findById(eventId)
                .orElseThrow(EventNotFoundException::new);

        boolean success = participantCounter.increase(eventId, event.getMaxParticipants());
        if(!success) {
            throw new MaxParticipantsExceededException();
        }

        OneHundredEventParticipant participant = OneHundredEventParticipant.builder()
                .eventId(eventId)
                .memberId(memberId)
                .status(ParticipantStatus.PENDING)
                .expiredAt(LocalDateTime.now().plusMinutes(5))
                .build();

        participantRepository.save(participant);

    }
}

