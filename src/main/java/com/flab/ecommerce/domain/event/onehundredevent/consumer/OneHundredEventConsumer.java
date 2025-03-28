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

@Slf4j
@Component
@RequiredArgsConstructor
public class OneHundredEventConsumer {

    private final EventRepository eventRepository;
    private final EventQueue eventQueue;
    private final EventPolicy eventPolicy;

    @Scheduled(fixedRate = 1000)
    public void consumeQueue() {
        log.info("[consumeQueue] 실행됨 - 큐에서 이벤트 처리 시도");

        List<Long> activeEventIds = eventRepository.findActiveOneHundredEventIds();

        for (Long eventId : activeEventIds) {
            Optional<Long> memberIdOpt = eventQueue.popFromQueue(eventId);

            memberIdOpt.ifPresent(memberId -> {
                log.info("[Queue Consumer] 이벤트 처리 중 - 이벤트 ID: {}, 유저 ID: {}", eventId, memberId);
                try {
                    eventPolicy.applyEvent(eventId, memberId);
                } catch (Exception e) {
                    log.error("[Queue Consumer] 이벤트 처리 중 오류 발생 - 이벤트 ID: {}, 유저 ID: {}, 오류: {}", eventId, memberId, e.getMessage());
                }
            });

            if (memberIdOpt.isEmpty()) {
                log.info("[consumeQueue] 이벤트 ID: {}에 대한 큐에 처리할 이벤트가 없습니다.", eventId);
            }
        }
    }
}


