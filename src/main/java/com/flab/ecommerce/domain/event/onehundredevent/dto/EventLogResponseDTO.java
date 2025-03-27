package com.flab.ecommerce.domain.event.onehundredevent.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EventLogResponseDTO {
    private Long eventId;
    private long totalParticipants;
    private long confirmedParticipants;
    private long pendingParticipants;
    private long canceledParticipants;
}