package com.flab.ecommerce.domain.event.onehundredevent.counter.inmemory;

import com.flab.ecommerce.domain.event.onehundredevent.counter.EventCounter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;


public class AtomicMapCounter implements EventCounter {
    private final Map<Long, AtomicInteger> counterMap = new ConcurrentHashMap<>();

    @Override
    public boolean increase(Long eventId, long maxParticipants) {
        AtomicInteger counter = counterMap.computeIfAbsent(eventId, id -> new AtomicInteger(0));
        if (counter.get() >= maxParticipants) return false;
        counter.incrementAndGet();
        return true;
    }

    @Override
    public void decrease(Long eventId) {
        AtomicInteger counter = counterMap.computeIfAbsent(eventId, id -> new AtomicInteger(0));
        counter.updateAndGet(c -> Math.max(c - 1, 0));
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


