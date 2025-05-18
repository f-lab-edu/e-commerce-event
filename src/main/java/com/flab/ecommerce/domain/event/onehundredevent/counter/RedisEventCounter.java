package com.flab.ecommerce.domain.event.onehundredevent.counter;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Primary
public class RedisEventCounter implements EventCounter {

    private final StringRedisTemplate redisTemplate;
    private final MeterRegistry meterRegistry;
    private static final String COUNTER_KEY_PREFIX = "event:counter:";

    private String getKey(Long eventId) {
        return COUNTER_KEY_PREFIX + eventId;
    }

    @Override
    public boolean increase(Long eventId, long maxParticipants) {
        Long count = redisTemplate.opsForValue().increment(getKey(eventId));
        Tags tags = Tags.of("eventId", String.valueOf(eventId));
        if (count != null) {
            meterRegistry.summary("event.participant.count", tags).record(count);
        }
        if (count != null && count > maxParticipants)  {
            redisTemplate.opsForValue().decrement(getKey(eventId));
            meterRegistry.counter("event.counter.increase.failure", tags).increment();
            return false;
        }
        meterRegistry.counter("event.counter.increase.success", tags).increment();
        return true;
    }

    @Override
    public void decrease(Long eventId) {
        redisTemplate.opsForValue().decrement(getKey(eventId));
    }

    @Override
    public long getCount(Long eventId) {
        String value = redisTemplate.opsForValue().get(getKey(eventId));
        return value != null ? Long.parseLong(value) : 0;
    }

    @Override
    public void reset(Long eventId) {
        redisTemplate.delete(getKey(eventId));
    }

    @Override
    public void reset() {
    }
}
