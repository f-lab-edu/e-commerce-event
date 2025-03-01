package com.flab.ecommerce.domain.event.dto;

import com.flab.ecommerce.domain.event.enums.EventType;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class EventUpdateRequestDTO {

    private String name;
    private String description;
    private String type;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
