
package com.flab.ecommerce.domain.event.onehundredevent.service;

import com.flab.ecommerce.domain.event.entity.Event;
import com.flab.ecommerce.domain.event.enums.EventStatus;
import com.flab.ecommerce.domain.event.enums.EventType;
import com.flab.ecommerce.domain.event.onehundredevent.counter.EventCounter;
import com.flab.ecommerce.domain.event.onehundredevent.entity.OneHundredEventParticipant;
import com.flab.ecommerce.domain.event.onehundredevent.processor.AsyncSaveProcessor;
import com.flab.ecommerce.domain.event.onehundredevent.repository.OneHundredEventRepository;
import com.flab.ecommerce.domain.event.policy.EventPolicy;
import com.flab.ecommerce.domain.event.queue.EventQueue;
import com.flab.ecommerce.domain.event.repository.EventRepository;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = "spring.task.scheduling.enabled=false")
@ActiveProfiles("test")
@AutoConfigureMockMvc
//@Transactional
class OneHundredEventIntegrationKafkaTest {
    @Autowired
    Environment env;
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
    private EventCounter oneHundredEventCounter;

    @Autowired
    private AsyncSaveProcessor saveProcessor;


    @BeforeEach
    void clearInMemoryData() {
      //  participantRepository.deleteAll();
       // eventRepository.deleteAll();
        eventQueue.clearAll();             // 큐 초기화
        oneHundredEventCounter.reset();    // 카운터 초기화
    }

    @Test
    void 로딩된_환경설정_확인() {
        System.out.println("Active Profiles: " + Arrays.toString(env.getActiveProfiles()));
        System.out.println("Kafka bootstrap-servers: " + env.getProperty("spring.kafka.bootstrap-servers"));
        System.out.println("Redis host: " + env.getProperty("spring.data.redis.host"));
        System.out.println("DB URL: " + env.getProperty("spring.datasource.url"));
    }

    @Test
    @DisplayName("100원 이벤트 - 큐 등록부터 정책 처리까지 전체 흐름 테스트")
    void 사용자_큐참가_이후_컨슈머_처리까지_흐름_확인() throws Exception {
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

        // when: 큐에 참여
        oneHundredEventService.addToQueue(eventId, memberId);

        Thread.sleep(1000);

        // then: 참가자 저장 확인
        List<OneHundredEventParticipant> participants = participantRepository.findByEventId(eventId);
        assertThat(participants).hasSize(1);
        assertThat(participants.get(0).getMemberId()).isEqualTo(memberId);
    }

    @Test
    @DisplayName("Kafka 전체 흐름 - 100명 큐 등록 후 Kafka 컨슈머로 참가자 처리")
    void kafka_기반_전체흐름_테스트() throws Exception {
        // given
        Event event = eventRepository.save(Event.builder()
                .type(EventType.ONE_HUNDRED_WON_DEAL)
                .startDate(LocalDateTime.now().minusMinutes(1))
                .endDate(LocalDateTime.now().plusMinutes(10))
                .maxParticipants(10)
                .status(EventStatus.ACTIVE)
                .name("카프카 이벤트")
                .description("Kafka 테스트용")
                .build());

        Long eventId = event.getId();

        int totalUsers = 10;
        ExecutorService executor = Executors.newFixedThreadPool(8);
        CountDownLatch latch = new CountDownLatch(totalUsers);

        for (long i = 1; i <= totalUsers; i++) {
            long memberId = i;
            executor.submit(() -> {
                try {
                    oneHundredEventService.addToQueue(eventId, memberId); // Kafka publish
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        // 컨슈머는 Kafka Listener에서 비동기 처리되므로 대기
        Awaitility.await()
                .atMost(Duration.ofSeconds(10))
                .pollInterval(Duration.ofMillis(500))
                .untilAsserted(() -> {
                    List<OneHundredEventParticipant> participants = participantRepository.findByEventId(eventId);
                    assertThat(participants.size()).isEqualTo(10);
                });
    }

    @Test
    @DisplayName("Kafka 중복된 100명 참가 - 최대 100명만 저장되어야 함")
    void kafka_중복_참가자_정책_검증() throws Exception {
        // given
        Event event = eventRepository.save(Event.builder()
                .type(EventType.ONE_HUNDRED_WON_DEAL)
                .startDate(LocalDateTime.now().minusMinutes(1))
                .endDate(LocalDateTime.now().plusMinutes(10))
                .maxParticipants(100)
                .status(EventStatus.ACTIVE)
                .name("중복 Kafka")
                .description("중복 Kafka 테스트")
                .build());

        Long eventId = event.getId();
        int iterTimes = 10;
        int totalUsers = 100;

        // when - 중복으로 Kafka publish
        for (long i = 1; i <= totalUsers; i++) {
            for (int j = 0; j < iterTimes; j++) {
                oneHundredEventService.addToQueue(eventId, i); // Kafka로 전송
            }
        }

        // then - Awaitility로 Kafka 처리 대기 후 검증
        Awaitility.await()
                .atMost(Duration.ofSeconds(10))
                .pollInterval(Duration.ofMillis(500))
                .untilAsserted(() -> {
                    List<OneHundredEventParticipant> participants = participantRepository.findByEventId(eventId);
                    assertThat(participants.size()).isEqualTo(100);
                });

        System.out.println("최종 DB 저장 수: " + participantRepository.countByEventId(eventId));
        System.out.println("카운터 수: " + oneHundredEventCounter.getCount(eventId));
    }

    @Test
    @DisplayName("Kafka 기반 중복 100명 동시 큐 등록 + 컨슈머 처리 완료 기준 TPS 테스트")
    void kafka_이벤트_병렬_등록_및_정확한_소비_시간_측정_테스트() throws Exception {
        // given
        Event event = eventRepository.save(Event.builder()
                .type(EventType.ONE_HUNDRED_WON_DEAL)
                .startDate(LocalDateTime.now().minusMinutes(1))
                .endDate(LocalDateTime.now().plusMinutes(10))
                .maxParticipants(100)
                .status(EventStatus.ACTIVE)
                .name("카프카 100원딜")
                .description("카프카 기반 중복 테스트용")
                .build());

        int totalUsers = 100;
        int iterTimes = 10;
        int coreCount = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(coreCount * 2);
        CountDownLatch latch = new CountDownLatch(totalUsers * iterTimes);

        // --- 큐 등록 시작 시간 ---
        long enqueueStart = System.currentTimeMillis();

        for (long i = 1; i <= totalUsers; i++) {
            long memberId = i;
            for (int j = 0; j < iterTimes; j++) {
                executor.submit(() -> {
                    try {
                        oneHundredEventService.addToQueue(event.getId(), memberId);
                    } finally {
                        latch.countDown();
                    }
                });
            }
        }

        latch.await();
        executor.shutdown();

        long enqueueEnd = System.currentTimeMillis();

        // --- 처리 완료 대기 ---
        long consumeStart = System.currentTimeMillis();

        Awaitility.await()
                .atMost(Duration.ofSeconds(15))
                .pollInterval(Duration.ofMillis(500))
                .until(() -> participantRepository.countByEventId(event.getId()) >= event.getMaxParticipants());

        long consumeEnd = System.currentTimeMillis();

        // then
        List<OneHundredEventParticipant> participants = participantRepository.findByEventId(event.getId());
        assertThat(participants.size()).isEqualTo(event.getMaxParticipants());

        // --- 결과 출력 ---
        long enqueueDuration = enqueueEnd - enqueueStart;
        long consumeDuration = consumeEnd - consumeStart;
        long totalDuration = consumeEnd - enqueueStart;
        double tps = participants.size() / (consumeDuration / 1000.0);

        System.out.println("✅ Kafka TPS 테스트 결과");
        System.out.println("등록 시간 (enqueue): " + enqueueDuration + "ms");
        System.out.println("소비 처리 시간 (consume): " + consumeDuration + "ms");
        System.out.println("총 소요 시간: " + totalDuration + "ms");
        System.out.println("TPS (소비 기준): " + String.format("%.2f", tps) + "건/초");
        System.out.println("DB 저장 수: " + participants.size());
    }


}

