package com.flab.ecommerce.domain.event.queue;

import java.util.Optional;

public interface EventQueue {
    boolean addToQueue(Long eventId, Long memberId);
    Optional<Long> popFromQueue(Long eventId);
    int getQueueSize(Long eventId);
    boolean isUserInQueue(Long eventId, Long memberId);
    void removeUserFromQueue(Long eventId, Long memberId);
    void clearAll();
}
