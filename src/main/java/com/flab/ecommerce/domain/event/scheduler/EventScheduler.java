package com.flab.ecommerce.domain.event.scheduler;

import com.flab.ecommerce.domain.event.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@Slf4j
@RequiredArgsConstructor
public class EventScheduler {

    private final EventRepository eventRepository;

    @Scheduled(cron = "0 0 * * * ?")
    @Transactional
    public void startEvents() {
        int updated = eventRepository.startScheduledEvents(LocalDateTime.now());
        if (updated > 0) {
            log.info("{}개의 이벤트가 시작되었습니다.", updated);
        } else {
            log.debug("시작할 이벤트가 없습니다.");
        }
    }

    @Scheduled(cron = "0 0 * * * ?")
    @Transactional
    public void closeEvents() {
        int updated = eventRepository.closeScheduledEvents(LocalDateTime.now());
        if (updated > 0) {
            log.info("{}개의 이벤트가 종료되었습니다.", updated);
        } else {
            log.debug("종료할 이벤트가 없습니다.");
        }
    }
}
