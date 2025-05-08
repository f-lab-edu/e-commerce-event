package com.flab.ecommerce.domain.event.onehundredevent.counter;

import com.flab.ecommerce.domain.event.counter.EventCounter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@Slf4j
public class OneHundredEventCounter implements EventCounter {
    private final Map<Long, AtomicInteger> counterMap = new ConcurrentHashMap<>();
    private final MeterRegistry meterRegistry;

    public OneHundredEventCounter(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }


    @Override
    public boolean increase(Long eventId, long maxParticipants) {
        AtomicInteger counter = counterMap.computeIfAbsent(eventId, id -> {
            AtomicInteger newCounter = new AtomicInteger(0);
            meterRegistry.gauge("event.participant.count", Tags.of("eventId", String.valueOf(id)), newCounter);
            return newCounter;
        });

        int current = counter.incrementAndGet();
        if (current > maxParticipants) {
            counter.decrementAndGet();
            meterRegistry.counter("event.counter.increase.failure", "eventId", String.valueOf(eventId)).increment();
            return false;
        }
        return true;
    }

    @Override
    public void decrease(Long eventId) {
        AtomicInteger counter = counterMap.computeIfAbsent(eventId, id -> new AtomicInteger(0));
        counter.updateAndGet(c -> Math.max(c - 1, 0));
        log.info("[카운터 감소] eventId={}, 감소 후 카운터={}", eventId, counterMap.get(eventId).get());

    }

    @Override
    public long getCount(Long eventId) {
        return counterMap.getOrDefault(eventId, new AtomicInteger(0)).get();
    }

    @Override
    public void reset(Long eventId) {
        counterMap.remove(eventId);
    }

    @Override
    public void reset() {
        counterMap.clear();
    }
}
