package com.flab.ecommerce.domain.event.onehundredevent.processor;

import com.flab.ecommerce.domain.event.onehundredevent.entity.OneHundredEventParticipant;

public interface SaveProcessor {
    void enqueue(OneHundredEventParticipant participant);
}
