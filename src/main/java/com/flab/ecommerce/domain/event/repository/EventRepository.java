package com.flab.ecommerce.domain.event.repository;

import com.flab.ecommerce.domain.event.entity.Event;
import com.flab.ecommerce.domain.event.enums.EventStatus;
import io.lettuce.core.dynamic.annotation.Param;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByStatus(EventStatus status);

    @Modifying
    @Query("UPDATE Event e SET e.status = 'ACTIVE' WHERE e.status = 'READY' AND e.startDate <= :now")
    int startScheduledEvents(LocalDateTime now);

    @Modifying
    @Query("UPDATE Event e SET e.status = 'CLOSED' WHERE e.status = 'ACTIVE' AND e.endDate <= :now")
    int closeScheduledEvents(LocalDateTime now);

    @Query("SELECT e.maxParticipants FROM Event e WHERE e.id = :eventId")
    Optional<Integer> findMaxParticipantsByEventId(Long eventId);

    @Query("SELECT e.id FROM Event e WHERE e.type = 'ONE_HUNDRED_WON_DEAL' AND e.status = 'ACTIVE'")
    List<Long> findActiveOneHundredEventIds();

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT e FROM Event e WHERE e.id = :id")
    Optional<Event> findWithPessimisticLockById(@Param("id") Long id);
}
