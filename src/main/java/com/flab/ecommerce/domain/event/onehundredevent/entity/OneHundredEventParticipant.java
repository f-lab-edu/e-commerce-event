package com.flab.ecommerce.domain.event.onehundredevent.entity;

import com.flab.ecommerce.domain.event.entity.Event;
import com.flab.ecommerce.domain.event.onehundredevent.enums.ParticipantStatus;
import com.flab.ecommerce.domain.event.onehundredevent.exception.NotInPendingStateException;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "event_participants_one_hundred")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class OneHundredEventParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long eventId;

    @Column(nullable = false)
    private Long memberId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ParticipantStatus status;

    @Column(nullable = false)
    private LocalDateTime expiredAt;

    public void confirmParticipation() {
        if (this.status != ParticipantStatus.PENDING) {
            throw new NotInPendingStateException();
        }
        this.status = ParticipantStatus.CONFIRMED;
    }

    public void cancelParticipation() {
        this.status = ParticipantStatus.CANCELED;
    }

}
