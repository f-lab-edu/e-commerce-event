package com.flab.ecommerce.domain.event.onehundredevent.service;

import com.flab.ecommerce.domain.event.entity.Event;
import com.flab.ecommerce.domain.event.enums.EventType;
import com.flab.ecommerce.domain.event.exception.EventNotFoundException;
import com.flab.ecommerce.domain.event.exception.InvalidEventTypeException;
import com.flab.ecommerce.domain.event.onehundredevent.entity.OneHundredEventParticipant;
import com.flab.ecommerce.domain.event.onehundredevent.enums.ParticipantStatus;
import com.flab.ecommerce.domain.event.onehundredevent.exception.AlreadyInQueueException;
import com.flab.ecommerce.domain.event.exception.AlreadyParticipatedException;
import com.flab.ecommerce.domain.event.onehundredevent.exception.NotInPendingStateException;
import com.flab.ecommerce.domain.event.onehundredevent.repository.OneHundredEventRepository;
import com.flab.ecommerce.domain.event.policy.EventPolicy;
import com.flab.ecommerce.domain.event.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OneHundredEventService {


    private final EventRepository eventRepository;
    private final OneHundredEventRepository oneHundredEventRepository;
    private final OneHundredEventQueueService oneHundredEventQueueService;
    private final EventPolicy eventPolicy;

    @Transactional
    public void addToQueue(Long eventId, Long memberId) {
        validateEvent(eventId);
        validateQueue(eventId, memberId);
        oneHundredEventQueueService.addToQueue(eventId, memberId);

        eventPolicy.applyEvent(eventId);
    }

    @Transactional
    public void confirmParticipation(Long eventId, Long memberId) {
        OneHundredEventParticipant participant = validateParticipant(eventId, memberId);
        validateEvent(eventId);
        participant.confirmParticipation();
    }

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void cancelExpiredParticipants() {
        List<OneHundredEventParticipant> expiredParticipants = oneHundredEventRepository.findExpiredParticipants(LocalDateTime.now());

        for (OneHundredEventParticipant participant : expiredParticipants) {
            log.info("[참가 취소] 이벤트 ID: {}, 유저 ID: {}", participant.getEventId(), participant.getMemberId());

            Event event = eventRepository.findById(participant.getEventId())
                    .orElseThrow(EventNotFoundException::new);

            participant.cancelParticipation();
            event.restoreEventStock();
            oneHundredEventRepository.save(participant);
            oneHundredEventQueueService.removeUserFromQueue(participant.getEventId(), participant.getMemberId());

            log.info("[스케줄러 완료] 만료된 참가자 취소 완료");
        }
    }

    private void validateEvent(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(EventNotFoundException::new);

        if (event.getType() != EventType.ONE_HUNDRED_WON_DEAL) {
            throw new InvalidEventTypeException();
        }
    }

    private void validateQueue(Long eventId, Long memberId) {
        if (oneHundredEventQueueService.isUserInQueue(eventId, memberId)) {
            throw new AlreadyInQueueException();
        }

        if (oneHundredEventRepository.existsByEventIdAndMemberId(eventId, memberId)) {
            throw new AlreadyParticipatedException();
        }
    }

    private OneHundredEventParticipant validateParticipant(Long eventId, Long memberId) {
        OneHundredEventParticipant participant = oneHundredEventRepository
                .findByEventIdAndMemberId(eventId, memberId)
                .orElseThrow(NotInPendingStateException::new);

        if (participant.getStatus() != ParticipantStatus.PENDING) {
            throw new NotInPendingStateException();
        }
        return participant;
    }
}
