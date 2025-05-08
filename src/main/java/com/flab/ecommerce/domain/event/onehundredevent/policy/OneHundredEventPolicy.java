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
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class OneHundredEventPolicy extends EventPolicy {

    private final OneHundredEventRepository participantRepository;
    private final EventRepository eventRepository;
    private final OneHundredEventCounter participantCounter;
    private final MeterRegistry meterRegistry;


    @Override
    @Transactional
    public void applyEvent(Long eventId, Long memberId) {

        Event event = eventRepository.findById(eventId)
                .orElseThrow(EventNotFoundException::new);

        long start = System.currentTimeMillis();
        if (participantRepository.existsByEventIdAndMemberId(eventId, memberId)) {
            return;
        }
        long existsCheckTime = System.currentTimeMillis() - start;
        meterRegistry.timer("event.duplication_check").record(Duration.ofMillis(existsCheckTime));

        boolean success = participantCounter.increase(eventId, event.getMaxParticipants());
        if (!success) {
            throw new MaxParticipantsExceededException();
        }

        OneHundredEventParticipant participant = OneHundredEventParticipant.builder()
                .eventId(eventId)
                .memberId(memberId)
                .status(ParticipantStatus.PENDING)
                .expiredAt(LocalDateTime.now().plusMinutes(5))
                .build();

        long saveStart = System.currentTimeMillis();
        participantRepository.save(participant);
        long saveTime = System.currentTimeMillis() - saveStart;
        meterRegistry.timer("event.save_time").record(Duration.ofMillis(saveTime));
        meterRegistry.counter("event.participation.success").increment();

    }


}

