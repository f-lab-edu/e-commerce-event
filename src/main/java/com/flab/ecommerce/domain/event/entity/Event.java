package com.flab.ecommerce.domain.event.entity;

import com.flab.ecommerce.domain.event.dto.EventUpdateRequestDTO;
import com.flab.ecommerce.domain.event.enums.EventStatus;
import com.flab.ecommerce.domain.event.enums.EventType;
import com.flab.ecommerce.domain.event.exception.EventAlreadyEndedException;
import com.flab.ecommerce.domain.event.exception.EventNotYetStartedException;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "events")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@EntityListeners(AuditingEntityListener.class)
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventType type;

    @Column(nullable = false)
    private LocalDateTime startDate;

    @Column(nullable = false)
    private LocalDateTime endDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventStatus status;

    @Column(nullable = false)
    private int maxParticipants;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public void update(EventUpdateRequestDTO requestDTO) {
        this.name = requestDTO.getName();
        this.description = requestDTO.getDescription();
        this.type = requestDTO.getType();
        this.startDate = requestDTO.getStartDate();
        this.endDate = requestDTO.getEndDate();
    }

    public void changeStatus(EventStatus status) {
        this.status = status;
    }

    public long getMaxParticipants() {
        return this.maxParticipants;
    }

    public boolean isStarted() {
        return this.startDate.isBefore(LocalDateTime.now());
    }

    public boolean isEnded() {
        return this.endDate.isBefore(LocalDateTime.now());
    }

    public void validateAvailablePeriod() {
        LocalDateTime now = LocalDateTime.now();
        if (startDate.isAfter(now)) {
            throw new EventNotYetStartedException();
        }
        if (endDate.isBefore(now)) {
            throw new EventAlreadyEndedException();
        }
    }

}
