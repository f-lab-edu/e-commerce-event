package com.flab.ecommerce.domain.event.queue;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Slf4j
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

        Boolean isFirst = redisTemplate.opsForValue().setIfAbsent(dupKey, "1", Duration.ofMinutes(5));

        if (Boolean.FALSE.equals(isFirst)) {
            return false;
        }

        try {
            redisTemplate.opsForList().leftPush(queueKey, String.valueOf(memberId));
            return true;
        } catch (Exception e) {
            redisTemplate.delete(dupKey);
            log.error("[큐 등록 실패] eventId={}, memberId={}, 이유: {}", eventId, memberId, e.getMessage(), e);
            throw e;
        }

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
        Set<Long> result = new HashSet<>();
        String pattern = QUEUE_PREFIX + "*";

        ScanOptions scanOptions = ScanOptions.scanOptions()
                .match(pattern)
                .count(1000)
                .build();

        RedisConnection connection = redisTemplate.getConnectionFactory().getConnection();
        try (Cursor<byte[]> cursor = connection.scan(scanOptions)) {
            while (cursor.hasNext()) {
                String key = new String(cursor.next());
                String eventIdStr = key.replace(QUEUE_PREFIX, "");
                try {
                    result.add(Long.parseLong(eventIdStr));
                } catch (NumberFormatException e) {
                    log.warn("[eventId 파싱 실패] key={}, 이유: {}", key, e.getMessage());
                }
            }
        } catch (Exception e) {
            log.error("[SCAN 실패] pattern={}, 예외: {}", pattern, e.getMessage(), e);
        }

        return result;
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


