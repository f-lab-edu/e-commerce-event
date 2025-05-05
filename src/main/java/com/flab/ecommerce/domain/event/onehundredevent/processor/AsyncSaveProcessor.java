package com.flab.ecommerce.domain.event.onehundredevent.processor;

import com.flab.ecommerce.domain.event.onehundredevent.counter.OneHundredEventCounter;
import com.flab.ecommerce.domain.event.onehundredevent.entity.OneHundredEventParticipant;
import com.flab.ecommerce.domain.event.onehundredevent.repository.OneHundredEventRepository;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Slf4j
@Component
@RequiredArgsConstructor
public class AsyncSaveProcessor {

    private final OneHundredEventRepository participantRepository;
    private final OneHundredEventCounter participantCounter;
    private final MeterRegistry meterRegistry;
    private final BlockingQueue<OneHundredEventParticipant> saveQueue = new LinkedBlockingQueue<>(10000);
    private static final int BATCH_SIZE = 100;

    public void enqueue(OneHundredEventParticipant participant) {
        if (!saveQueue.offer(participant)) {
            log.warn("[저장 큐 초과] 큐가 가득 찼습니다. 저장 실패 - eventId={}, memberId={}",
                    participant.getEventId(), participant.getMemberId());
            meterRegistry.counter("event.save_queue_full").increment();
            participantCounter.decrease(participant.getEventId());
        }
    }

    @Scheduled(fixedRate = 100)
    public void batchSave() {
        List<OneHundredEventParticipant> batch = new ArrayList<>();
        saveQueue.drainTo(batch, BATCH_SIZE);

        if (!batch.isEmpty()) {
            for (OneHundredEventParticipant p : batch) {
                try {
                    long saveStart = System.currentTimeMillis();
                    participantRepository.save(p);
                    long saveTime = System.currentTimeMillis() - saveStart;

                    meterRegistry.timer("event.save_time.batch").record(Duration.ofMillis(saveTime));
                    meterRegistry.counter("event.save.success").increment();
                    meterRegistry.counter("event.participation.success").increment();

                } catch (DataIntegrityViolationException dup) {
                    log.warn("[중복 저장 차단됨] eventId={}, memberId={}", p.getEventId(), p.getMemberId());
                    meterRegistry.counter("event.save.duplicate").increment();
                    participantCounter.decrease(p.getEventId());

                } catch (Exception e) {
                    log.error("[배치 저장 실패] 재시도 대상 - eventId={}, memberId={}", p.getEventId(), p.getMemberId(), e);
                    enqueue(p);
                }
            }
        }
    }
}
