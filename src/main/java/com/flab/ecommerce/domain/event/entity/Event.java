package com.flab.ecommerce.domain.event.entity;

import com.flab.ecommerce.domain.event.dto.EventUpdateRequestDTO;
import com.flab.ecommerce.domain.event.enums.EventStatus;
import com.flab.ecommerce.domain.event.enums.EventType;
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

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public void update(EventUpdateRequestDTO requestDTO) {
        if(requestDTO.getName() != null) {
            this.name = requestDTO.getName();
        }

        if (requestDTO.getDescription() != null) {
            this.description = requestDTO.getDescription();
        }

        if (requestDTO.getStartDate() != null && requestDTO.getEndDate() != null) {
            this.startDate = requestDTO.getStartDate();
            this.endDate = requestDTO.getEndDate();
        }

        if (requestDTO.getType() != null) {
            this.type = EventType.fromCode(requestDTO.getType());
        }
    }

    public void changeStatue(EventStatus status) {
        this.status = status;
    }

}
