package com.flab.ecommerce.domain.event.onehundredevent.consumer;

import com.flab.ecommerce.domain.event.onehundredevent.entity.OneHundredEventParticipant;
import com.flab.ecommerce.domain.event.onehundredevent.message.EventMessage;
import com.flab.ecommerce.domain.event.onehundredevent.repository.OneHundredEventRepository;
import com.flab.ecommerce.domain.event.policy.EventPolicy;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Profile("kafka")
@Slf4j
@Component
@RequiredArgsConstructor
public class OneHundredEventKafkaConsumer {

    private final EventPolicy eventPolicy;
    private final MeterRegistry meterRegistry;
    private final RedisTemplate<String, String> redisTemplate;
    private final OneHundredEventRepository oneHundredEventRepository;
    @Value("${event.consumer.type}")
    private String consumerType;

    @KafkaListener(topics = "event-participant", groupId = "event-participant-group")
    public void consume(List<EventMessage> eventMessages) {
        List<OneHundredEventParticipant> participants = new ArrayList<>();
        for (EventMessage eventMessage : eventMessages) {
            Long eventId = eventMessage.getEventId();
            Long memberId = eventMessage.getMemberId();

            String dupKey = "dup:one_hundred_event:" + eventId + ":" + memberId;
            Boolean isFirst = redisTemplate.opsForValue().setIfAbsent(dupKey, "1", Duration.ofMinutes(5));

            if (Boolean.FALSE.equals(isFirst)) {
                log.info("[중복 참가자 감지] eventId={}, memberId={}", eventId, memberId);
                meterRegistry.counter("event.kafka.duplicate", "eventId", String.valueOf(eventId)).increment();
                continue;
            }

//        try {
//            long start = System.currentTimeMillis();
//
//            eventPolicy.applyEvent(eventId, memberId);
//            long duration = System.currentTimeMillis() - start;
//            meterRegistry.timer("event.consumer.processing.time", "eventId", String.valueOf(eventId))
//                    .record(Duration.ofMillis(duration));
//
//            meterRegistry.counter("event.consumer.processed", "eventId", String.valueOf(eventId)).increment();
//
//        } catch (DataIntegrityViolationException e) {
//            meterRegistry.counter("event.participation.duplicate", "eventId", String.valueOf(eventId)).increment();
//            log.warn("[중복 참가자] eventId={}, memberId={}", eventId, memberId);
//
//        } catch (Exception e) {
//            log.error("[Kafka 소비 실패] eventId={}, memberId={}, 예외={}", eventId, memberId, e.getMessage(), e);
//        }
            try {
                OneHundredEventParticipant participant = eventPolicy.applyEvent(eventId, memberId);
                participants.add(participant);
            } catch (Exception e) {
                log.error("[Kafka 소비 실패] eventId={}, memberId={}, 예외={}", eventId, memberId, e.getMessage(), e);
            }
        }

        if (!participants.isEmpty()) {
            if ("kafka".equals(consumerType)) {
                oneHundredEventRepository.saveAll(participants);
                meterRegistry.counter("event.save.batch.success").increment();
            } else {
                log.warn("[Kafka 소비 실패] consumerType이 kafka가 아닙니다. 현재 consumerType: {}", consumerType);
                meterRegistry.counter("event.save.batch.failure").increment();
            }
        }
    }




}







