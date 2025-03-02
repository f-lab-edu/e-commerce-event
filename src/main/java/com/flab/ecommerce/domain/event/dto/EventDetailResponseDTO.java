package com.flab.ecommerce.domain.event.dto;

import com.flab.ecommerce.domain.event.entity.Event;
import com.flab.ecommerce.domain.event.enums.EventType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class EventDetailResponseDTO {
    private Long id;
    private String name;
    private String description;
    private EventType type;
    private String status;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    public EventDetailResponseDTO(Event event) {
        this.id = event.getId();
        this.name = event.getName();
        this.description = event.getDescription();
        this.type = event.getType();
        this.status = event.getStatus().name();
        this.startDate = event.getStartDate();
        this.endDate = event.getEndDate();
    }
}
