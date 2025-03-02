package com.flab.ecommerce.domain.event.repository;

import com.flab.ecommerce.domain.event.entity.Event;
import com.flab.ecommerce.domain.event.enums.EventStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByStatus(EventStatus status);
}
