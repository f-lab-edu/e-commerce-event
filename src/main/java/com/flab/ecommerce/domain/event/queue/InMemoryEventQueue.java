package com.flab.ecommerce.domain.event.queue;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Primary
public class InMemoryEventQueue implements EventQueue {
    private final Map<Long, Queue<Long>> eventQueueMap = new HashMap<>();

    @Override
    public synchronized boolean addToQueue(Long eventId, Long memberId) {
        eventQueueMap.computeIfAbsent(eventId, k -> new LinkedList<>()).add(memberId);
        return true;
    }

    @Override
    public synchronized Optional<Long> popFromQueue(Long eventId) {
        Queue<Long> queue = eventQueueMap.get(eventId);
        if(queue == null || queue.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(queue.poll());
    }

    @Override
    public synchronized int getQueueSize(Long eventId) {
        return eventQueueMap.getOrDefault(eventId, new LinkedList<>()).size();
    }

    public synchronized boolean isUserInQueue(Long eventId, Long memberId) {
        Queue<Long> queue = eventQueueMap.get(eventId);
        return queue != null && queue.contains(memberId);
    }

    @Override
    public synchronized void removeUserFromQueue(Long eventId, Long memberId) {
        Queue<Long> queue = eventQueueMap.get(eventId);
        if (queue != null) {
            queue.remove(memberId);
        }
    }
}
