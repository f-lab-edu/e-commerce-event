package com.flab.ecommerce.domain.event.onehundredevent.counter;

import com.flab.ecommerce.domain.event.counter.EventCounter;

import java.util.HashMap;
import java.util.Map;


public class SynchronizedMapCounter implements EventCounter {
    private final Map<Long, Integer> counterMap = new HashMap<>();

    @Override
    public boolean increase(Long eventId, long maxParticipants) {
        synchronized (counterMap) {
            int count = counterMap.getOrDefault(eventId, 0);
            if (count >= maxParticipants) return false;
            counterMap.put(eventId, count + 1);
            return true;
        }
    }

    @Override
    public void decrease(Long eventId) {
        synchronized (counterMap) {
            int count = counterMap.getOrDefault(eventId, 0);
            counterMap.put(eventId, Math.max(count - 1, 0));
        }
    }

    @Override
    public long getCount(Long eventId) {
        synchronized (counterMap) {
            return counterMap.getOrDefault(eventId, 0);
        }
    }

    @Override
    public void reset(Long eventId) {
        synchronized (counterMap) {
            counterMap.remove(eventId);
        }
    }

    @Override
    public void reset() {
        counterMap.clear();
    }
}
