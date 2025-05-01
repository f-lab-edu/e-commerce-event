/*
package com.flab.ecommerce.domain.event.onehundredevent.service;

import com.flab.ecommerce.domain.event.entity.Event;
import com.flab.ecommerce.domain.event.enums.EventStatus;
import com.flab.ecommerce.domain.event.enums.EventType;
import com.flab.ecommerce.domain.event.onehundredevent.counter.OneHundredEventCounter;
import com.flab.ecommerce.domain.event.onehundredevent.entity.OneHundredEventParticipant;
import com.flab.ecommerce.domain.event.onehundredevent.repository.OneHundredEventRepository;
import com.flab.ecommerce.domain.event.policy.EventPolicy;
import com.flab.ecommerce.domain.event.queue.EventQueue;
import com.flab.ecommerce.domain.event.repository.EventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = "spring.task.scheduling.enabled=false")
@ActiveProfiles("dev")
@AutoConfigureMockMvc
//@Transactional

class OneHundredEventIntegrationTest {
    @Autowired
    private OneHundredEventService oneHundredEventService;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private OneHundredEventRepository participantRepository;
    @Autowired
    private EventQueue eventQueue;
    @Autowired
    private EventPolicy eventPolicy;
    @Autowired
    private OneHundredEventCounter oneHundredEventCounter;

    @BeforeEach
    void clearInMemoryData() {
        participantRepository.deleteAll();
        eventRepository.deleteAll();
        eventQueue.clearAll();             // 큐 초기화
        oneHundredEventCounter.reset();    // 카운터 초기화
    }

    @Test
    @DisplayName("100원 이벤트 - 큐 등록부터 정책 처리까지 전체 흐름 테스트")
    void 사용자_큐참가_이후_컨슈머_처리까지_흐름_확인() {
        // given
        Event event = Event.builder()
                .type(EventType.ONE_HUNDRED_WON_DEAL)
                .startDate(LocalDateTime.now().minusMinutes(1))
                .endDate(LocalDateTime.now().plusMinutes(10))
                .maxParticipants(100)
                .status(EventStatus.ACTIVE)
                .name("100원딜")
                .description("테스트")
                .build();
        eventRepository.save(event);

        Long eventId = event.getId();
        Long memberId = 123L;
123L
        // when: 큐에 참여
        oneHundredEventService.addToQueue(eventId, memberId);

        // then: 큐에 잘 들어갔는지 확인
        assertThat(eventQueue.isUserInQueue(eventId, memberId)).isTrue();

        // when: 컨슈머 역할 - 큐에서 pop 후 정책 적용
        eventPolicy.applyEvent(eventId, memberId);

        // then: 참가자 저장 확인
        List<OneHundredEventParticipant> participants = participantRepository.findByEventId(eventId);
        assertThat(participants).hasSize(1);
        assertThat(participants.get(0).getMemberId()).isEqualTo(memberId);
    }

    @Test
    @DisplayName("1000명 동시 참가 후 정책 적용 테스트")
    void 천명_참가자_큐_등록_및_DB저장_검증() throws Exception {
        long totalStart = System.currentTimeMillis();

        // given
        Event event = Event.builder()
                .type(EventType.ONE_HUNDRED_WON_DEAL)
                .startDate(LocalDateTime.now().minusMinutes(1))
                .endDate(LocalDateTime.now().plusMinutes(10))
                .maxParticipants(2000)
                .status(EventStatus.ACTIVE)
                .name("100원 DEAL")
                .description("동시 참가 1000")
                .build();
        eventRepository.save(event);
        Long eventId = event.getId();

        int totalUsers = 3000;
        int coreCount = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(coreCount * 2);
        CountDownLatch latch = new CountDownLatch(totalUsers);

        for (long i = 1; i <= totalUsers; i++) {
            long memberId = i;
            executor.submit(() -> {
                try {
                    oneHundredEventService.addToQueue(eventId, memberId);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        long consumeStart = System.currentTimeMillis();
        int queueSize = eventQueue.getQueueSize(eventId);
        ExecutorService applyExecutor = Executors.newFixedThreadPool(coreCount * 2);
        CountDownLatch applyLatch = new CountDownLatch(queueSize);

        for (int i = 0; i < queueSize; i++) {
            Long memberId = eventQueue.popFromQueue(eventId).orElse(null);
            if (memberId != null) {
                applyExecutor.submit(() -> {
                    try {
                        eventPolicy.applyEvent(eventId, memberId);
                    } finally {
                        applyLatch.countDown();
                    }
                });
            } else {
                applyLatch.countDown();
            }
        }

        applyLatch.await();
        applyExecutor.shutdown();
        long consumeEnd = System.currentTimeMillis();

        // then - 정합성 검증
        List<OneHundredEventParticipant> participants = participantRepository.findByEventId(eventId);
        int totalParticipants = participants.size();
        long expectedMax = event.getMaxParticipants();

        System.out.println(" 최종 참가자 수: " + totalParticipants);
        assertThat((long)totalParticipants)
                .as("참가자는 최대 인원 수를 넘으면 안됨")
                .isLessThanOrEqualTo(expectedMax);

        long totalEnd = System.currentTimeMillis();
        long duration = consumeEnd - consumeStart;
        double tps = totalParticipants / (duration / 1000.0);

        System.out.println("전체 소요 시간: " + (totalEnd - totalStart) + "ms");
        System.out.println("컨슈머 처리 시간: " + duration + "ms");
        System.out.println("TPS (초당 처리량): " + String.format("%.2f", tps) + "건/초");
    }


    @Test
    @DisplayName("두 이벤트 병렬 처리 - 락 범위 좁힘 + TPS 측정")
    void 두개의_이벤트_병렬_처리_테스트() throws Exception {
        long totalStart = System.currentTimeMillis();

        // given - 이벤트 2개 생성
        Event event1 = eventRepository.save(Event.builder()
                .type(EventType.ONE_HUNDRED_WON_DEAL)
                .startDate(LocalDateTime.now().minusMinutes(1))
                .endDate(LocalDateTime.now().plusMinutes(10))
                .maxParticipants(2000)
                .status(EventStatus.ACTIVE)
                .name("이벤트 1")
                .description("첫 번째 이벤트")
                .build());

        Event event2 = eventRepository.save(Event.builder()
                .type(EventType.ONE_HUNDRED_WON_DEAL)
                .startDate(LocalDateTime.now().minusMinutes(1))
                .endDate(LocalDateTime.now().plusMinutes(10))
                .maxParticipants(2000)
                .status(EventStatus.ACTIVE)
                .name("이벤트 2")
                .description("두 번째 이벤트")
                .build());

        int totalUsersPerEvent = 3000;
        int coreCount = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(coreCount * 2);
        CountDownLatch latch = new CountDownLatch(totalUsersPerEvent * 2);

        // 큐 등록
        for (int i = 1; i <= totalUsersPerEvent; i++) {
            long memberId1 = i;
            long memberId2 = i + 10000;
            executor.submit(() -> {
                try {
                    oneHundredEventService.addToQueue(event1.getId(), memberId1);
                } finally {
                    latch.countDown();
                }
            });

            executor.submit(() -> {
                try {
                    oneHundredEventService.addToQueue(event2.getId(), memberId2);
                } finally {
                    latch.countDown();
                }
            });
        }


        latch.await();
        executor.shutdown();

        // 컨슈머 처리 시작
        long consumeStart = System.currentTimeMillis();
        ExecutorService applyExecutor = Executors.newFixedThreadPool(coreCount * 2);
        CountDownLatch applyLatch = new CountDownLatch(totalUsersPerEvent * 2);

        for (int i = 0; i < totalUsersPerEvent * 2; i++) {
            Long eventId =  i % 2 == 0 ? event1.getId() : event2.getId();
            Long memberId = eventQueue.popFromQueue(eventId).orElse(null);
            if (memberId != null) {
                applyExecutor.submit(() -> {
                    try {
                        eventPolicy.applyEvent(eventId, memberId);
                    } finally {
                        applyLatch.countDown();
                    }
                });
            } else {
                applyLatch.countDown();
            }
        }

        applyLatch.await();
        applyExecutor.shutdown();

        long consumeEnd = System.currentTimeMillis();
        long totalEnd = System.currentTimeMillis();

        // then - 정합성 확인
        long participants1 = participantRepository.countByEventId(event1.getId());
        long participants2 = participantRepository.countByEventId(event2.getId());

        System.out.println("이벤트 1 참가자 수: " + participants1);
        System.out.println("이벤트 2 참가자 수: " + participants2);

        assertThat(participants1).isLessThanOrEqualTo(event1.getMaxParticipants());
        assertThat(participants2).isLessThanOrEqualTo(event2.getMaxParticipants());

        // 성능 로그 출력
        long duration = consumeEnd - consumeStart;
        long totalDuration = totalEnd - totalStart;
        double totalParticipants = participants1 + participants2;
        double tps = totalParticipants / (duration / 1000.0);

        System.out.println("컨슈머 처리 시간: " + duration + "ms");
        System.out.println("전체 소요 시간: " + totalDuration + "ms");
        System.out.println("TPS (초당 처리량): " + String.format("%.2f", tps) + "건/초");
    }

}
*/
