package com.flab.ecommerce.domain.event.onehundredevent.consumer;

import com.flab.ecommerce.domain.event.policy.EventPolicy;
import com.flab.ecommerce.domain.event.queue.EventQueue;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Comparator;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Component
@RequiredArgsConstructor
public class OneHundredEventConsumer {

    private final EventQueue eventQueue;
    private final EventPolicy eventPolicy;
    private final MeterRegistry meterRegistry;
    int coreCount = Runtime.getRuntime().availableProcessors();
    private final ExecutorService executor = Executors.newFixedThreadPool(coreCount);
    private static final int MAX_PROCESS_PER_THREAD = 50;

    //@Async
    @Scheduled(fixedRate = 1000)
    public void consumeQueue() {

        for (int i = 0; i < coreCount; i++) {
            executor.submit(() -> {
                Set<Long> eventIds = eventQueue.getRegisteredEventIds();
                Long eventId = pickEventId(eventIds);

                if (eventId == null) {
                    return;
                }

                meterRegistry.gauge("event.queue.size",
                        Tags.of("eventId", String.valueOf(eventId)),
                        eventQueue,
                        q -> q.getQueueSize(eventId)
                );

                int processed = 0;
                while (processed < MAX_PROCESS_PER_THREAD) {
                    Optional<Long> memberIdOpt = eventQueue.popFromQueue(eventId);

                    if (memberIdOpt.isEmpty()) {
                        break;
                    }

                    Long memberId = memberIdOpt.get();
                    try {
                        long start = System.currentTimeMillis();
                        eventPolicy.applyEvent(eventId, memberId);
                        long duration = System.currentTimeMillis() - start;

                        meterRegistry.timer("event.consumer.processing.time", "eventId", String.valueOf(eventId))
                                .record(Duration.ofMillis(duration));

                        processed++;

                    } catch (Exception e) {
                        log.info("[이벤트 처리 실패] eventId={}, memberId={}, 예외 타입={}, 메시지={}",
                                eventId, memberId, e.getClass().getSimpleName(), e.getMessage(), e);
                    }
                }

                meterRegistry.counter("event.consumer.processed", "eventId", String.valueOf(eventId))
                        .increment(processed);
            });
        }
    }


    private Long pickEventId(Set<Long> eventIds) {
        return eventIds.stream()
                .filter(eventId -> eventQueue.getQueueSize(eventId) > 0)
                .max(Comparator.comparingInt(eventQueue::getQueueSize))
                .orElse(null);
    }
}



