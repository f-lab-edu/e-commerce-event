package com.flab.ecommerce.domain.event.onehundredevent.processor;

import com.flab.ecommerce.domain.event.onehundredevent.entity.OneHundredEventParticipant;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("kafka")
public class DummySaveProcessor implements SaveProcessor {
    @Override
    public void enqueue(OneHundredEventParticipant participant) {
        // Kafka에서는 이걸 사용하지 않음
    }
}
