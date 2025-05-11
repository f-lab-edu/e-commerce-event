package com.flab.ecommerce.domain.event.queue;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;
import java.util.Set;

@Primary
@Component
@RequiredArgsConstructor
public class RedisEventQueue implements EventQueue {

    private final RedisTemplate<String, String> redisTemplate;
    private static final String QUEUE_PREFIX = "queue:one_hundred_event:";
    private static final String DUPLICATE_PREFIX = "dup:one_hundred_event:";

    @Override
    public boolean addToQueue(Long eventId, Long memberId) {
        String queueKey = getQueueKey(eventId);
        String dupKey = getDupKey(eventId, memberId);

        //키가 없으면 넣고 true 반환
        Boolean isFirst = redisTemplate.opsForValue().setIfAbsent(dupKey, "1", Duration.ofMinutes(5));
        if (Boolean.FALSE.equals(isFirst)) return false;

        redisTemplate.opsForList().leftPush(queueKey, String.valueOf(memberId));
        return true;
    }

    @Override
    public Optional<Long> popFromQueue(Long eventId) {
        String queueKey = getQueueKey(eventId);
        String memberId = redisTemplate.opsForList().rightPop(queueKey);
        return Optional.ofNullable(memberId).map(Long::parseLong);
    }

    @Override
    public int getQueueSize(Long eventId) {
        Long size = redisTemplate.opsForList().size(getQueueKey(eventId));
        return size != null ? size.intValue() : 0;
    }

    @Override
    public void removeUserFromQueue(Long eventId, Long memberId) {
        redisTemplate.opsForList().remove(getQueueKey(eventId), 1, String.valueOf(memberId));
        redisTemplate.delete(getDupKey(eventId, memberId));
    }

    @Override
    public void clearAll() {
    }

    @Override
    public Set<Long> getRegisteredEventIds() {
        return Set.of();
    }

    @Override
    public boolean isUserInQueue(Long eventId, Long memberId) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(getDupKey(eventId, memberId)));
    }

    private String getQueueKey(Long eventId) {
        return QUEUE_PREFIX + eventId;
    }

    private String getDupKey(Long eventId, Long memberId) {
        return DUPLICATE_PREFIX + eventId + ":" + memberId;
    }
}


