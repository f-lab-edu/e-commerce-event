package com.flab.ecommerce.domain.event.onehundredevent.consumer;

import com.flab.ecommerce.domain.event.policy.EventPolicy;
import com.flab.ecommerce.domain.event.queue.EventQueue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

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
    int coreCount = Runtime.getRuntime().availableProcessors();
    private final ExecutorService executor = Executors.newFixedThreadPool(coreCount * 2);
    private static final int MAX_PROCESS_PER_THREAD = 20;

    @Async
    @Scheduled(fixedRate = 1000)
    public void consumeQueue() {
        log.info("[consumeQueue] 실행됨");

        for(int i =0; i< coreCount; i++) {
            executor.submit(() -> {
                Set<Long> eventIds = eventQueue.getRegisteredEventIds();
                Long eventId = pickEventId(eventIds);
                if(eventId == null) return;

                int processed = 0;
                while (processed < MAX_PROCESS_PER_THREAD) {
                    Optional<Long> memberIdOpt = eventQueue.popFromQueue(eventId);
                    if (memberIdOpt.isEmpty()) break;

                    Long memberId = memberIdOpt.get();
                    try {
                        eventPolicy.applyEvent(eventId, memberId);
                        processed++;
                        log.info("이벤트 처리 완료 - 이벤트 ID: {}, 유저 ID: {}", eventId, memberId);
                    } catch (Exception e) {
                        log.error("이벤트 처리 실패 - 이벤트 ID: {}, 유저 ID: {}, 에러: {}", eventId, memberId, e.getMessage());
                    }
                }

                log.info("[Worker] 처리 종료 - 이벤트 ID: {}, 처리 수: {}", eventId, processed);
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



