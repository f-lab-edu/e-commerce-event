package com.flab.ecommerce.domain.event.counter;

public interface EventCounter {
    boolean increase(Long eventId, long maxParticipants);
    void decrease(Long eventId);
    long getCount(Long eventId);
    void reset(Long eventId);
    void reset();
}
