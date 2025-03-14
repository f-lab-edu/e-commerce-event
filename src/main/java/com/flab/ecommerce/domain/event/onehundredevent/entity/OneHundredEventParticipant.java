package com.flab.ecommerce.domain.event.onehundredevent.entity;

import com.flab.ecommerce.domain.event.onehundredevent.enums.ParticipantStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    public void confirmParticipation() {
        if (this.status != ParticipantStatus.PENDING) {
            throw new IllegalStateException("결제 대기 상태가 아닌 경우 확정할 수 없습니다.");
        }
        this.status = ParticipantStatus.CONFIRMED;
    }

    public void cancelParticipation() {
        this.status = ParticipantStatus.CANCELED;
    }

}
