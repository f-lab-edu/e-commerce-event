package com.flab.ecommerce.domain.event.queue;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Primary
public class InMemoryEventQueue implements EventQueue {
    private final Map<Long, LinkedHashSet<Long>> eventQueueMap = new HashMap<>();

    @Override
    public synchronized boolean addToQueue(Long eventId, Long memberId) {
        LinkedHashSet<Long> queue = eventQueueMap.computeIfAbsent(eventId, k -> new LinkedHashSet<>());
        return queue.add(memberId);
    }

    @Override
    public synchronized Optional<Long> popFromQueue(Long eventId) {
        LinkedHashSet<Long> queue = eventQueueMap.get(eventId);
        if (queue == null || queue.isEmpty()) {
            return Optional.empty();
        }
        Iterator<Long> iter = queue.iterator();
        Long first = iter.next();
        iter.remove();
        return Optional.of(first);
    }

    @Override
    public synchronized int getQueueSize(Long eventId) {
        return eventQueueMap.getOrDefault(eventId, new LinkedHashSet<>()).size();
    }

    @Override
    public synchronized boolean isUserInQueue(Long eventId, Long memberId) {
        LinkedHashSet<Long> queue = eventQueueMap.get(eventId);
        return queue != null && queue.contains(memberId);
    }

    @Override
    public synchronized void removeUserFromQueue(Long eventId, Long memberId) {
        LinkedHashSet<Long> queue = eventQueueMap.get(eventId);
        if (queue != null) queue.remove(memberId);
    }

    @Override
    public synchronized void clearAll() {
        eventQueueMap.clear();
    }

    @Override
    public synchronized Set<Long> getRegisteredEventIds() {
        return new HashSet<>(eventQueueMap.keySet());
    }
}

