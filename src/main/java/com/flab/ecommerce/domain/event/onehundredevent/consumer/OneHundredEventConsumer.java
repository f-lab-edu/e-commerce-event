package com.flab.ecommerce.domain.event.onehundredevent.consumer;

import com.flab.ecommerce.domain.event.policy.EventPolicy;
import com.flab.ecommerce.domain.event.queue.EventQueue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Component
@RequiredArgsConstructor
public class OneHundredEventConsumer {

    private final EventQueue eventQueue;
    private final EventPolicy eventPolicy;
    int coreCount = Runtime.getRuntime().availableProcessors();
    private final ExecutorService executor = Executors.newFixedThreadPool(coreCount * 2);
    private final Map<Long, Boolean> processingFlags = new ConcurrentHashMap<>();

    @Async
    @Scheduled(fixedRate = 1000)
    public void consumeQueue() {
        log.info("[consumeQueue] 실행됨");

        Set<Long> eventIds = eventQueue.getRegisteredEventIds();

        Long eventidToProcess = pickEventId(eventIds);
        if (eventidToProcess == null) return;

        Boolean previous = processingFlags.putIfAbsent(eventidToProcess, true);
        if (previous != null && previous) return;

        executor.submit(() -> {
            try {
                while (true) {
                    Optional<Long> memberIdOpt = eventQueue.popFromQueue(eventidToProcess);
                    log.info("큐에서 꺼낸 멤버: {}", memberIdOpt.orElse(null));
                    if (memberIdOpt.isEmpty()) break;
                    eventPolicy.applyEvent(eventidToProcess, memberIdOpt.get());
                }
            } finally {
                processingFlags.put(eventidToProcess, false);
                log.info("[consumeQueue] 종료됨");
            }
        });
    }


    private Long pickEventId(Set<Long> eventIds) {
        return eventIds.stream()
                .filter(eventId -> eventQueue.getQueueSize(eventId) > 0)
                .max(Comparator.comparingInt(eventQueue::getQueueSize))
                .orElse(null);
    }
}



