package com.flab.ecommerce.domain.event.onehundredevent.repository;

import com.flab.ecommerce.domain.event.onehundredevent.entity.OneHundredEventParticipant;
import com.flab.ecommerce.domain.event.onehundredevent.enums.ParticipantStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OneHundredEventRepository extends JpaRepository<OneHundredEventParticipant, Long> {

    boolean existsByEventIdAndMemberId(Long eventId, Long memberId);
    Optional<OneHundredEventParticipant> findByEventIdAndMemberId(Long eventId, Long memberId);

    @Query("SELECT p FROM OneHundredEventParticipant p WHERE p.status = 'PENDING' AND p.expiredAt < :now")
    List<OneHundredEventParticipant> findExpiredParticipants(LocalDateTime now);

    long countByEventId(Long eventId);

    long countByEventIdAndStatus(Long eventId, ParticipantStatus participantStatus);

    List<OneHundredEventParticipant> findByEventId(Long eventId);
}