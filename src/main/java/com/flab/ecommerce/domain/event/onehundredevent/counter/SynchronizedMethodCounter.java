package com.flab.ecommerce.domain.event.onehundredevent.counter;

import com.flab.ecommerce.domain.event.counter.EventCounter;

import java.util.HashMap;
import java.util.Map;

public class SynchronizedMethodCounter implements EventCounter {
    private final Map<Long, Integer> counterMap = new HashMap<>();

    @Override
    public synchronized boolean increase(Long eventId, long maxParticipants) {
        int count = counterMap.getOrDefault(eventId, 0);
        if (count >= maxParticipants) return false;
        counterMap.put(eventId, count + 1);
        return true;
    }

    @Override
    public synchronized void decrease(Long eventId) {
        int count = counterMap.getOrDefault(eventId, 0);
        counterMap.put(eventId, Math.max(count - 1, 0));
    }

    @Override
    public synchronized long getCount(Long eventId) {
        return counterMap.getOrDefault(eventId, 0);
    }

    @Override
    public synchronized void reset(Long eventId) {
        counterMap.remove(eventId);
    }
}
