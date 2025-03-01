package com.flab.ecommerce.domain.event.service;

import com.flab.ecommerce.domain.event.dto.EventCreateRequestDTO;
import com.flab.ecommerce.domain.event.dto.EventDetailResponseDTO;
import com.flab.ecommerce.domain.event.dto.EventUpdateRequestDTO;
import com.flab.ecommerce.domain.event.entity.Event;
import com.flab.ecommerce.domain.event.enums.EventStatus;
import com.flab.ecommerce.domain.event.enums.EventType;
import com.flab.ecommerce.domain.event.exception.EventNotFoundException;
import com.flab.ecommerce.domain.event.exception.InvalidEventPeriodException;
import com.flab.ecommerce.domain.event.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;

    @Transactional
    public EventDetailResponseDTO createEvent(EventCreateRequestDTO requestDTO) {
        validateEventPeriod(requestDTO.getStartDate(), requestDTO.getEndDate());

        EventType eventType = EventType.fromCode(requestDTO.getType());

        Event event = Event.builder()
                .name(requestDTO.getName())
                .description(requestDTO.getDescription())
                .type(eventType)
                .startDate(requestDTO.getStartDate())
                .endDate(requestDTO.getEndDate())
                .status(EventStatus.READY)
                .build();

        Event savedEvent = eventRepository.save(event);
        return new EventDetailResponseDTO(savedEvent);
    }

    @Transactional
    public EventDetailResponseDTO updateEvent(Long eventId, EventUpdateRequestDTO requestDTO) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(EventNotFoundException::new);

        if (requestDTO.getStartDate() != null && requestDTO.getEndDate() != null) {
            validateEventPeriod(requestDTO.getStartDate(), requestDTO.getEndDate());
        }

        event.update(requestDTO);

        return new EventDetailResponseDTO(event);
    }

    private void validateEventPeriod(LocalDateTime startDate, LocalDateTime endDate) {
        if(endDate.isBefore(startDate)) {
            throw new InvalidEventPeriodException(startDate, endDate);
        }
    }
}
