package com.flab.ecommerce.domain.event.onehundredevent.repository;

import com.flab.ecommerce.domain.event.onehundredevent.entity.OneHundredEventParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OneHundredEventRepository extends JpaRepository<OneHundredEventParticipant, Long> {

    boolean existsByEventIdAndMemberId(Long eventId, Long memberId);
    Optional<OneHundredEventParticipant> findByEventIdAndMemberId(Long eventId, Long memberId);
}