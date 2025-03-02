package com.flab.ecommerce.domain.event.dto;

import com.flab.ecommerce.domain.event.entity.Event;
import com.flab.ecommerce.domain.event.enums.EventStatus;
import com.flab.ecommerce.domain.event.enums.EventType;
import lombok.Getter;

@Getter
public class EventListResponseDTO {

    private Long id;
    private String name;
    private EventType type;
    private EventStatus status;

    public EventListResponseDTO(Event event) {
        this.id = event.getId();
        this.name = event.getName();
        this.type = event.getType();
        this.status = event.getStatus();
    }
}
