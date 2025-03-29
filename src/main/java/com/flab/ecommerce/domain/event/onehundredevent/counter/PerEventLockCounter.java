package com.flab.ecommerce.domain.event.onehundredevent.counter;

import com.flab.ecommerce.domain.event.counter.EventCounter;

import java.util.HashMap;
import java.util.Map;


public class PerEventLockCounter implements EventCounter {
    private final Map<Long, Integer> counterMap = new HashMap<>();
    private final Map<Long, Object> lockMap = new HashMap<>();

    private Object getLock(Long eventId) {
        synchronized (lockMap) {
            return lockMap.computeIfAbsent(eventId, id -> new Object());
        }
    }

    @Override
    public boolean increase(Long eventId, long maxParticipants) {
        synchronized (getLock(eventId)) {
            int count = counterMap.getOrDefault(eventId, 0);
            if (count >= maxParticipants) return false;
            counterMap.put(eventId, count + 1);
            return true;
        }
    }

    @Override
    public void decrease(Long eventId) {
        synchronized (getLock(eventId)) {
            int count = counterMap.getOrDefault(eventId, 0);
            counterMap.put(eventId, Math.max(count - 1, 0));
        }
    }

    @Override
    public long getCount(Long eventId) {
        synchronized (getLock(eventId)) {
            return counterMap.getOrDefault(eventId, 0);
        }
    }

    @Override
    public void reset(Long eventId) {
        synchronized (getLock(eventId)) {
            counterMap.remove(eventId);
        }
    }

    @Override
    public void reset() {
        counterMap.clear();
    }
}