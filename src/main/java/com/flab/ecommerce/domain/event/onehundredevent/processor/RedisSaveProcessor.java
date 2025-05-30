package com.flab.ecommerce.domain.event.onehundredevent.processor;

import com.flab.ecommerce.domain.event.onehundredevent.counter.EventCounter;
import com.flab.ecommerce.domain.event.onehundredevent.entity.OneHundredEventParticipant;
import com.flab.ecommerce.domain.event.onehundredevent.repository.OneHundredEventRepository;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

@Profile("redis")
@Slf4j
@Component
@RequiredArgsConstructor
public class RedisSaveProcessor implements SaveProcessor {

    private final OneHundredEventRepository participantRepository;
    private final EventCounter eventCounter;
    private final MeterRegistry meterRegistry;
    private final BlockingQueue<OneHundredEventParticipant> saveQueue = new LinkedBlockingQueue<>(10000);
    private static final int BATCH_SIZE = 500;
    private final ExecutorService executor = Executors.newFixedThreadPool(4);

    @PostConstruct
    public void init() {
        meterRegistry.gauge("event.save.queue.size", saveQueue, BlockingQueue::size);
    }

    @Override
    public void enqueue(OneHundredEventParticipant participant) {
        if (!saveQueue.offer(participant)) {
            log.warn("[저장 큐 초과] 큐가 가득 찼습니다. 저장 실패 - eventId={}, memberId={}",
                    participant.getEventId(), participant.getMemberId());
            meterRegistry.counter("event.save_queue_full").increment();
            eventCounter.decrease(participant.getEventId());
            log.info("[큐 등록] eventId={}, memberId={}, 큐사이즈={}", participant.getEventId(), participant.getMemberId(), saveQueue.size());

        }
    }

    public int getQueueSize() {
        return saveQueue.size();
    }


    @Scheduled(fixedRate = 100)
    public void batchSave() {
        List<OneHundredEventParticipant> batch = new ArrayList<>();
        saveQueue.drainTo(batch, BATCH_SIZE);

        if (!batch.isEmpty()) {
            executor.submit(() -> {
                try {
                    long saveStart = System.currentTimeMillis();
                    participantRepository.saveAll(batch);
                    long saveTime = System.currentTimeMillis() - saveStart;

                    for (OneHundredEventParticipant p : batch) {
                        meterRegistry.counter("event.save.success", "eventId", String.valueOf(p.getEventId())).increment();
                    }
                    meterRegistry.timer("event.save_time.batch").record(Duration.ofMillis(saveTime));

                } catch (DataIntegrityViolationException dup) {

                    for (OneHundredEventParticipant p : batch) {
                        meterRegistry.counter("event.save.duplicate").increment();
                        eventCounter.decrease(p.getEventId());
                    }

                } catch (Exception e) {
                    for (OneHundredEventParticipant p : batch) {
                        eventCounter.decrease(p.getEventId());
                    }
                }
            });
        }
    }

}
