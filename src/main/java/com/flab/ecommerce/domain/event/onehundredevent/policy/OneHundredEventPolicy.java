package com.flab.ecommerce.domain.event.onehundredevent.policy;

import com.flab.ecommerce.domain.event.entity.Event;
import com.flab.ecommerce.domain.event.exception.EventNotFoundException;
import com.flab.ecommerce.domain.event.exception.MaxParticipantsExceededException;
import com.flab.ecommerce.domain.event.onehundredevent.counter.OneHundredEventCounter;
import com.flab.ecommerce.domain.event.onehundredevent.duplicate.InMemoryDuplicateChecker;
import com.flab.ecommerce.domain.event.onehundredevent.entity.OneHundredEventParticipant;
import com.flab.ecommerce.domain.event.onehundredevent.enums.ParticipantStatus;
import com.flab.ecommerce.domain.event.onehundredevent.processor.AsyncSaveProcessor;
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
    private final AsyncSaveProcessor saveProcessor;
    private final InMemoryDuplicateChecker duplicateChecker;
    private final MeterRegistry meterRegistry;

    @Override
    @Transactional
    public void applyEvent(Long eventId, Long memberId) {
        long start = System.currentTimeMillis();
        Event event = eventRepository.findById(eventId)
                .orElseThrow(EventNotFoundException::new);

        if (duplicateChecker.isDuplicate(eventId, memberId)) {
            meterRegistry.counter("event.duplicate.memory_check", "eventId", String.valueOf(eventId)).increment();
            return;
        }

        boolean success = participantCounter.increase(eventId, event.getMaxParticipants());
        if (!success) {
            duplicateChecker.remove(eventId, memberId);
            meterRegistry.counter("event.participant.limit_exceeded", "eventId", String.valueOf(eventId)).increment();
            throw new MaxParticipantsExceededException();
        }

        OneHundredEventParticipant participant = OneHundredEventParticipant.builder()
                .eventId(eventId)
                .memberId(memberId)
                .status(ParticipantStatus.PENDING)
                .expiredAt(LocalDateTime.now().plusMinutes(5))
                .build();

        try {
            saveProcessor.enqueue(participant);
        } catch (Exception e) {
            participantCounter.decrease(eventId);
            duplicateChecker.remove(eventId, memberId);
            throw e;
        } finally {
            long duration = System.currentTimeMillis() - start;
            meterRegistry.timer("event.policy.apply.time", "eventId", String.valueOf(eventId))
                    .record(Duration.ofMillis(duration));
        }
    }
}