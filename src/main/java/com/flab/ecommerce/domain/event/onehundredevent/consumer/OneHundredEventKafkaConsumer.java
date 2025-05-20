package com.flab.ecommerce.domain.event.onehundredevent.consumer;

import com.flab.ecommerce.domain.event.onehundredevent.message.EventMessage;
import com.flab.ecommerce.domain.event.policy.EventPolicy;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Slf4j
@Component
@RequiredArgsConstructor
public class OneHundredEventKafkaConsumer {

    private final EventPolicy eventPolicy;
    private final MeterRegistry meterRegistry;
    private final RedisTemplate<String, String> redisTemplate;

    @KafkaListener(topics = "event-participant", groupId = "event-participant-group")
    public void consume(EventMessage eventMessage) {
        Long eventId = eventMessage.getEventId();
        Long memberId = eventMessage.getMemberId();

        String dupKey = "dup:one_hundred_event:" + eventId + ":" + memberId;
        Boolean isFirst = redisTemplate.opsForValue().setIfAbsent(dupKey, "1", Duration.ofMinutes(5));
        System.out.println("중복 체크 key=" + dupKey + ", isFirst=" + isFirst);

        if (Boolean.FALSE.equals(isFirst)) {
            log.info("[중복 참가자 감지] eventId={}, memberId={}", eventId, memberId);
            meterRegistry.counter("event.kafka.duplicate", "eventId", String.valueOf(eventId)).increment();
            return;
        }


        try {
            long start = System.currentTimeMillis();

            eventPolicy.applyEvent(eventId, memberId);
            long duration = System.currentTimeMillis() - start;
            meterRegistry.timer("event.consumer.processing.time", "eventId", String.valueOf(eventId))
                    .record(Duration.ofMillis(duration));

            meterRegistry.counter("event.consumer.processed", "eventId", String.valueOf(eventId)).increment();

        } catch (DataIntegrityViolationException e) {
            meterRegistry.counter("event.participation.duplicate", "eventId", String.valueOf(eventId)).increment();
            log.warn("[중복 참가자] eventId={}, memberId={}", eventId, memberId);

        } catch (Exception e) {
            log.error("[Kafka 소비 실패] eventId={}, memberId={}, 예외={}", eventId, memberId, e.getMessage(), e);
        }
    }


}







