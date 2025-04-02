package com.flab.ecommerce.domain.event.policy;

import org.springframework.transaction.annotation.Transactional;

public abstract class EventPolicy {
    @Transactional
    public abstract void applyEvent(Long eventId, Long memberId);
}
