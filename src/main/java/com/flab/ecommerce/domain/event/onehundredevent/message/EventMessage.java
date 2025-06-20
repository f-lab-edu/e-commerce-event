package com.flab.ecommerce.domain.event.onehundredevent.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventMessage {
    private Long eventId;
    private Long memberId;
}
