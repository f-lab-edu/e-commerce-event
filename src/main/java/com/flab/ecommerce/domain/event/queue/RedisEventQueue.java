package com.flab.ecommerce.domain.event.queue;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RedisEventQueue implements EventQueue {

    private final RedisTemplate<String, String> redisTemplate;
    private static final String QUEUE_PREFIX = "queue:one_hundred_event:";

    public boolean addToQueue(Long eventId, Long memberId) {

        if (isUserInQueue(eventId, memberId)) {
            return false;
        }

        String queueKey = QUEUE_PREFIX + eventId;
        redisTemplate.opsForList().leftPush(queueKey, String.valueOf(memberId));
        redisTemplate.persist(queueKey);
        redisTemplate.expire(queueKey, Duration.ofMinutes(5));
        return true;
    }

    public Optional<Long> popFromQueue(Long eventId) {
        String queueKey = QUEUE_PREFIX + eventId;
        String userId = redisTemplate.opsForList().leftPop(queueKey);
        return Optional.ofNullable(userId).map(Long::parseLong);
    }

    public int getQueueSize(Long eventId) {
        String queueKey = QUEUE_PREFIX + eventId;
        return Optional.ofNullable(redisTemplate.opsForList().size(queueKey)).orElse(0L).intValue();
    }

    public void removeUserFromQueue(Long eventId, Long memberId) {
        String queueKey = QUEUE_PREFIX + eventId;
        redisTemplate.opsForList().remove(queueKey, 1, String.valueOf(memberId));
    }

    public boolean isUserInQueue(Long eventId, Long memberId) {
        String queueKey = QUEUE_PREFIX + eventId;

        Boolean hasKey = redisTemplate.hasKey(queueKey);
        if (Boolean.FALSE.equals(hasKey)) {
            return false;
        }

        List<String> queueList = redisTemplate.opsForList().range(queueKey, 0, -1);
        return queueList.contains(String.valueOf(memberId));
    }
}

