package com.flab.ecommerce.domain.event.onehundredevent.duplicate;

import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class InMemoryDuplicateChecker {
    private final Set<String> duplicateSet = ConcurrentHashMap.newKeySet();

    public boolean isDuplicate(Long eventId, Long memberId) {
        return !duplicateSet.add(eventId + ":" + memberId);
    }

    public void remove(Long eventId, Long memberId) {
        duplicateSet.remove(eventId + ":" + memberId);
    }
}