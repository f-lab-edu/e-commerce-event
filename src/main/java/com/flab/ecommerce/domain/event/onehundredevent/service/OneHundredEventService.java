package com.flab.ecommerce.domain.event.onehundredevent.service;

import com.flab.ecommerce.domain.event.entity.Event;
import com.flab.ecommerce.domain.event.enums.EventType;
import com.flab.ecommerce.domain.event.exception.EventNotFoundException;
import com.flab.ecommerce.domain.event.onehundredevent.repository.OneHundredEventRepository;
import com.flab.ecommerce.domain.event.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OneHundredEventService {


    private final EventRepository eventRepository;
    private final OneHundredEventRepository oneHundredEventRepository;
    private final OneHundredEventQueueService oneHundredEventQueueService;

    @Transactional
    public void addToQueue(Long eventId, Long memberId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(EventNotFoundException::new);

        if(event.getType() != EventType.ONE_HUNDRED_WON_DEAL) {
            throw new IllegalArgumentException("100원 이벤트가 아닙니다.");
        }

        if (oneHundredEventQueueService.isUserInQueue(eventId, memberId)) {
            throw new IllegalStateException("이미 대기열에 등록된 사용자입니다.");
        }

        if (oneHundredEventRepository.existsByEventIdAndMemberId(eventId, memberId)) {
            throw new IllegalStateException("이미 참가한 이벤트입니다.");
        }

        oneHundredEventQueueService.addToQueue(eventId, memberId);
    }
}
