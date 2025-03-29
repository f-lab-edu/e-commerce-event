package com.flab.ecommerce.domain.event.onehundredevent.consumer;

import com.flab.ecommerce.domain.event.policy.EventPolicy;
import com.flab.ecommerce.domain.event.queue.EventQueue;
import com.flab.ecommerce.domain.event.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Component
@RequiredArgsConstructor
public class OneHundredEventConsumer {

    private final EventRepository eventRepository;
    private final EventQueue eventQueue;
    private final EventPolicy eventPolicy;
    private final ExecutorService executor = Executors.newFixedThreadPool(20);

    @Scheduled(fixedRate = 1000)
    public void consumeQueue() {
        log.info("[consumeQueue] 실행됨 - 큐에서 이벤트 처리 시도");

        List<Long> activeEventIds = eventRepository.findActiveOneHundredEventIds();

        for (Long eventId : activeEventIds) {
            executor.submit(() -> {
                while (true) {
                    Optional<Long> memberIdOpt = eventQueue.popFromQueue(eventId);
                    if (memberIdOpt.isEmpty()) break;

                    Long memberId = memberIdOpt.get();
                    try {
                        eventPolicy.applyEvent(eventId, memberId);
                        log.info("[Queue Consumer] 처리 완료 - 이벤트 ID: {}, 유저 ID: {}", eventId, memberId);
                    } catch (Exception e) {
                        log.error("[Queue Consumer] 처리 실패 - 이벤트 ID: {}, 유저 ID: {}, 오류: {}", eventId, memberId, e.getMessage());
                    }
                }
            });
        }
    }
}



