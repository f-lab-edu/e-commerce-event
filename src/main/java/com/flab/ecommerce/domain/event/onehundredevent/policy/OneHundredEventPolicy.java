package com.flab.ecommerce.domain.event.onehundredevent.policy;

import com.flab.ecommerce.domain.event.entity.Event;
import com.flab.ecommerce.domain.event.exception.EventStockNotEnoughException;
import com.flab.ecommerce.domain.event.exception.EventNotFoundException;
import com.flab.ecommerce.domain.event.onehundredevent.entity.OneHundredEventParticipant;
import com.flab.ecommerce.domain.event.onehundredevent.enums.ParticipantStatus;
import com.flab.ecommerce.domain.event.onehundredevent.repository.OneHundredEventRepository;
import com.flab.ecommerce.domain.event.onehundredevent.service.OneHundredEventQueueService;
import com.flab.ecommerce.domain.event.policy.EventPolicy;
import com.flab.ecommerce.domain.event.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OneHundredEventPolicy extends EventPolicy {

    private final OneHundredEventQueueService queueService;
    private final OneHundredEventRepository participantRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public void applyEvent(Long eventId) {
        Optional<Long> userId = queueService.popFromQueue(eventId);

        userId.ifPresent(memberId -> {
            Event event = eventRepository.findById(eventId)
                    .orElseThrow(EventNotFoundException::new);

            if (event.getEventStock() <= 0) {
                throw new EventStockNotEnoughException();
            }

            OneHundredEventParticipant participant = OneHundredEventParticipant.builder()
                    .eventId(event.getId())
                    .memberId(memberId)
                    .status(ParticipantStatus.PENDING)
                    .expiredAt(LocalDateTime.now().plusMinutes(5))
                    .build();

            participantRepository.save(participant);

            event.decreaseEventStock();
        });
    }
}
