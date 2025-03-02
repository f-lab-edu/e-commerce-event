package com.flab.ecommerce.domain.event.service;

import com.flab.ecommerce.domain.event.dto.EventCreateRequestDTO;
import com.flab.ecommerce.domain.event.dto.EventDetailResponseDTO;
import com.flab.ecommerce.domain.event.dto.EventListResponseDTO;
import com.flab.ecommerce.domain.event.dto.EventUpdateRequestDTO;
import com.flab.ecommerce.domain.event.entity.Event;
import com.flab.ecommerce.domain.event.enums.EventStatus;
import com.flab.ecommerce.domain.event.exception.EventNotFoundException;
import com.flab.ecommerce.domain.event.exception.InvalidEventPeriodException;
import com.flab.ecommerce.domain.event.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;

    public List<EventListResponseDTO> getAllEvents(EventStatus status) {
        if (status != null) {
            return eventRepository.findByStatus(status).stream()
                    .map(EventListResponseDTO::new)
                    .toList();
        } else {
            return eventRepository.findAll().stream()
                    .map(EventListResponseDTO::new)
                    .toList();
        }
    }

    public EventDetailResponseDTO getEventById(long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(EventNotFoundException::new);
        return new EventDetailResponseDTO(event);
    }

    @Transactional
    public EventDetailResponseDTO createEvent(EventCreateRequestDTO requestDTO) {
        validateEventPeriod(requestDTO.getStartDate(), requestDTO.getEndDate());

        Event event = Event.builder()
                .name(requestDTO.getName())
                .description(requestDTO.getDescription())
                .type(requestDTO.getType())
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

    @Transactional
    public void deleteEvent(long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(EventNotFoundException::new);
        eventRepository.delete(event);
    }

    @Transactional
    public EventDetailResponseDTO changeEventStatus(long id, EventStatus status) {
        Event event = eventRepository.findById(id)
                .orElseThrow(EventNotFoundException::new);

        event.changeStatus(status);

        return new EventDetailResponseDTO(event);
    }

    private void validateEventPeriod(LocalDateTime startDate, LocalDateTime endDate) {
        if (endDate.isBefore(startDate)) {
            throw new InvalidEventPeriodException(startDate, endDate);
        }
    }
}
