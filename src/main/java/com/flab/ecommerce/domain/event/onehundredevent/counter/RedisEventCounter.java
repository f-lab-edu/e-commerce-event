package com.flab.ecommerce.domain.event.onehundredevent.counter;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Primary
public class RedisEventCounter implements EventCounter {

    private final StringRedisTemplate redisTemplate;
    private static final String COUNTER_KEY_PREFIX = "event:counter:";

    private String getKey(Long eventId) {
        return COUNTER_KEY_PREFIX + eventId;
    }

    @Override
    public boolean increase(Long eventId, long maxParticipants) {
        Long count = redisTemplate.opsForValue().increment(getKey(eventId));
        if (count > maxParticipants) {
            redisTemplate.opsForValue().decrement(getKey(eventId));
            return false;
        }
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
