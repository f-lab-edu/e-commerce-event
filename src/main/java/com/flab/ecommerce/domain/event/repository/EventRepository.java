package com.flab.ecommerce.domain.event.repository;

import com.flab.ecommerce.domain.event.entity.Event;
import com.flab.ecommerce.domain.event.enums.EventStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByStatus(EventStatus status);

    @Modifying
    @Query("UPDATE Event e SET e.status = 'ACTIVE' WHERE e.status = 'READY' AND e.startDate <= :now")
    int startScheduledEvents(LocalDateTime now);

    @Modifying
    @Query("UPDATE Event e SET e.status = 'CLOSED' WHERE e.status = 'ACTIVE' AND e.endDate <= :now")
    int closeScheduledEvents(LocalDateTime now);
}
