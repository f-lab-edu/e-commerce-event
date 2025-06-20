package com.flab.ecommerce.domain.event.onehundredevent.policy;

import com.flab.ecommerce.domain.event.entity.Event;
import com.flab.ecommerce.domain.event.exception.EventNotFoundException;
import com.flab.ecommerce.domain.event.exception.MaxParticipantsExceededException;
import com.flab.ecommerce.domain.event.onehundredevent.counter.EventCounter;
import com.flab.ecommerce.domain.event.onehundredevent.entity.OneHundredEventParticipant;
import com.flab.ecommerce.domain.event.onehundredevent.enums.ParticipantStatus;
import com.flab.ecommerce.domain.event.onehundredevent.processor.SaveProcessor;
import com.flab.ecommerce.domain.event.onehundredevent.repository.OneHundredEventRepository;
import com.flab.ecommerce.domain.event.policy.EventPolicy;
import com.flab.ecommerce.domain.event.repository.EventRepository;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class OneHundredEventPolicy extends EventPolicy {

    private final EventRepository eventRepository;
    private final EventCounter eventCounter;
    private final OneHundredEventRepository oneHundredEventRepository;
    private final SaveProcessor saveProcessor;
    //private final InMemoryDuplicateChecker duplicateChecker;
    private final MeterRegistry meterRegistry;
    private final RedisTemplate<String, String> redisTemplate;
    @Value("${event.consumer.type}")
    private String consumerType;

    @Override
    @Transactional
    public OneHundredEventParticipant applyEvent(Long eventId, Long memberId) {
        meterRegistry.counter("event.policy.apply.count", "eventId", String.valueOf(eventId)).increment();
        long start = System.currentTimeMillis();
        Event event = eventRepository.findById(eventId)
                .orElseThrow(EventNotFoundException::new);

//        if (duplicateChecker.isDuplicate(eventId, memberId)) {
//            meterRegistry.counter("event.duplicate.memory_check", "eventId", String.valueOf(eventId)).increment();
//            return;
//        }

        boolean success = eventCounter.increase(eventId, event.getMaxParticipants());
        if (!success) {
//            duplicateChecker.remove(eventId, memberId);
            meterRegistry.counter("event.participant.limit_exceeded", "eventId", String.valueOf(eventId)).increment();
            throw new MaxParticipantsExceededException();
        }

        OneHundredEventParticipant participant = OneHundredEventParticipant.builder()
                .eventId(eventId)
                .memberId(memberId)
                .status(ParticipantStatus.PENDING)
                .expiredAt(LocalDateTime.now().plusMinutes(5))
                .build();

        long duration = System.currentTimeMillis() - start;
        meterRegistry.timer("event.policy.apply.time", "eventId", String.valueOf(eventId))
                .record(Duration.ofMillis(duration));

        return participant;  // 저장은 하지 않는다

//        try {
//            if ("kafka".equals(consumerType)) {
//                long saveStart = System.currentTimeMillis();
//                oneHundredEventRepository.save(participant);
//                long saveTime = System.currentTimeMillis() - saveStart;
//                meterRegistry.counter("event.save.success", "eventId", String.valueOf(eventId)).increment();
//                meterRegistry.timer("event.save.time", "eventId", String.valueOf(eventId))
//                        .record(Duration.ofMillis(saveTime));
//            } else {
//                saveProcessor.enqueue(participant);
//            }
//        } catch (Exception e) {
//            eventCounter.decrease(eventId);
////            duplicateChecker.remove(eventId, memberId);
//         //   String dupKey = "dup:one_hundred_event:" + eventId + ":" + memberId;
//          //  redisTemplate.delete(dupKey);
//            throw e;
//        } finally {
//            long duration = System.currentTimeMillis() - start;
//            meterRegistry.timer("event.policy.apply.time", "eventId", String.valueOf(eventId))
//                    .record(Duration.ofMillis(duration));
//        }
    }

    @Transactional
    public void saveAll(List<OneHundredEventParticipant> participants) {
        oneHundredEventRepository.saveAll(participants);
    }
}