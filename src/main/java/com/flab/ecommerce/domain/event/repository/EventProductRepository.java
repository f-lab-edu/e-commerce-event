package com.flab.ecommerce.domain.event.repository;

import com.flab.ecommerce.domain.event.entity.EventProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventProductRepository extends JpaRepository<EventProduct, Long> {
    List<EventProduct> findByEventId(Long eventId);
}
