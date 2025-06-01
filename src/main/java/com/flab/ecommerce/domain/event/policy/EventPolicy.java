package com.flab.ecommerce.domain.event.policy;

import com.flab.ecommerce.domain.event.onehundredevent.entity.OneHundredEventParticipant;
import org.springframework.transaction.annotation.Transactional;

public abstract class EventPolicy {
    @Transactional
    public abstract OneHundredEventParticipant applyEvent(Long eventId, Long memberId);
}
