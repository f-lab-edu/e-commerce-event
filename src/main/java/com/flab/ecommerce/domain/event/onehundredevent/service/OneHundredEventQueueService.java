package com.flab.ecommerce.domain.event.onehundredevent.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OneHundredEventQueueService {

    private final RedisTemplate<String, String> redisTemplate;
    private static final String QUEUE_PREFIX = "queue:one_hundred_event:";

    public void addToQueue(Long eventId, Long memberId) {
        String queueKey = QUEUE_PREFIX + eventId;
        redisTemplate.opsForList().leftPush(queueKey, String.valueOf(memberId));
        redisTemplate.persist(queueKey);
        redisTemplate.expire(queueKey, Duration.ofMinutes(5));
    }

    public Optional<Long> popFromQueue(Long eventId) {
        String queueKey = QUEUE_PREFIX + eventId;
        String userId = redisTemplate.opsForList().leftPop(queueKey);
        return Optional.ofNullable(userId).map(Long::parseLong);
    }

    public Long getQueueSize(Long eventId) {
        String queueKey = QUEUE_PREFIX + eventId;
        return redisTemplate.opsForList().size(queueKey);
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

        List<String> queueList = Optional.ofNullable(redisTemplate.opsForList().range(queueKey, 0, -1))
                .orElse(Collections.emptyList());
        return queueList.contains(String.valueOf(memberId));
    }
}

