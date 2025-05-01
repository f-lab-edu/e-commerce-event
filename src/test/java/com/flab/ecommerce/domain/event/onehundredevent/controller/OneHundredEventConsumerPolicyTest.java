//package com.flab.ecommerce.domain.event.onehundredevent.controller;
//
//import com.flab.ecommerce.domain.event.entity.Event;
//import com.flab.ecommerce.domain.event.enums.EventStatus;
//import com.flab.ecommerce.domain.event.enums.EventType;
//import com.flab.ecommerce.domain.event.onehundredevent.entity.OneHundredEventParticipant;
//import com.flab.ecommerce.domain.event.onehundredevent.repository.OneHundredEventRepository;
//import com.flab.ecommerce.domain.event.queue.EventQueue;
//import com.flab.ecommerce.domain.event.repository.EventRepository;
//import com.flab.ecommerce.domain.member.entity.Member;
//import com.flab.ecommerce.domain.member.repository.MemberRepository;
//import lombok.extern.slf4j.Slf4j;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.concurrent.TimeUnit;
//
//import static com.flab.ecommerce.domain.member.enums.MemberRole.USER;
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.awaitility.Awaitility.await;
//import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
//import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@Slf4j
//@SpringBootTest
//@AutoConfigureMockMvc
//class OneHundredEventConsumerPolicyTest {
//
//    @Autowired private MockMvc mockMvc;
//    @Autowired private EventRepository eventRepository;
//    @Autowired private MemberRepository memberRepository;
//    @Autowired private OneHundredEventRepository participantRepository;
//    @Autowired private EventQueue eventQueue;
//
//    private Event createEvent(String name) {
//        return eventRepository.save(Event.builder()
//                .type(EventType.ONE_HUNDRED_WON_DEAL)
//                .startDate(LocalDateTime.now().minusMinutes(1))
//                .endDate(LocalDateTime.now().plusMinutes(10))
//                .maxParticipants(100)
//                .status(EventStatus.ACTIVE)
//                .name(name)
//                .description("설명")
//                .build());
//    }
//
//    private void registerUsersToQueue(Long eventId, int count) throws Exception {
//        for (int i = 0; i < count; i++) {
//            Member member = memberRepository.save(Member.builder()
//                    .email("user" + eventId + "_" + i + "@test.com")
//                    .password("pw")
//                    .name("user" + i)
//                    .role(USER)
//                    .build());
//
//            mockMvc.perform(post("/events/{eventId}/queue", eventId)
//                            .with(user(member.getEmail()).roles("USER")))
//                    .andExpect(status().isOk());
//        }
//    }
//
//    @Test
//    @DisplayName("여러 스레드가 하나의 큐를 병렬로 처리할 수 있다")
//    void 여러_스레드_같은_큐_병렬_처리_검증() throws Exception {
//        Event event = createEvent("sharedQueue");
//        registerUsersToQueue(event.getId(), 100);
//
//        await().atMost(3, TimeUnit.SECONDS).untilAsserted(() -> {
//            List<OneHundredEventParticipant> participants = participantRepository.findByEventId(event.getId());
//            log.info("[Test] 참여자 수: {}", participants.size());
//
//            assertThat(participants.size()).isGreaterThan(20);
//        });
//    }
//
//    @Test
//    @DisplayName("한 스레드는 최대 N건만 처리한다")
//    void 한_스레드당_최대_20건_처리_검증() throws Exception {
//        Event event = createEvent("event");
//        registerUsersToQueue(event.getId(), 100);
//
//        await().atMost(3, TimeUnit.SECONDS).untilAsserted(() -> {
//            List<OneHundredEventParticipant> participants = participantRepository.findByEventId(event.getId());
//            int maxPerThread = 20;
//            int maxThreads = Runtime.getRuntime().availableProcessors();
//            log.info("[Test] 참여자 수: {} (최대 허용: {})", participants.size(), maxPerThread * maxThreads);
//            assertThat(participants.size()).isLessThanOrEqualTo(maxPerThread * maxThreads);
//        });
//    }
//
//    @Test
//    @DisplayName("가장 긴 큐가 우선 처리된다")
//    void 가장_긴_큐_우선_처리_검증() throws Exception {
//        Event shortQueue = createEvent("shortQueue");
//        Event longQueue = createEvent("longQueue");
//
//        registerUsersToQueue(shortQueue.getId(), 3);
//        registerUsersToQueue(longQueue.getId(), 30);
//
//        await().atMost(3, TimeUnit.SECONDS).untilAsserted(() -> {
//            List<OneHundredEventParticipant> shortParticipants = participantRepository.findByEventId(shortQueue.getId());
//            List<OneHundredEventParticipant> longParticipants = participantRepository.findByEventId(longQueue.getId());
//
//            log.info("[Test] longQueue 처리 수: {}, shortQueue 처리 수: {}", longParticipants.size(), shortParticipants.size());
//
//            assertThat(longParticipants).isNotEmpty();
//            assertThat(shortParticipants).isEmpty();
//        });
//    }
//
//    @Test
//    @DisplayName("여러 이벤트 큐를 병렬로 각각 처리할 수 있다")
//    void 여러_이벤트_큐_병렬_처리_검증() throws Exception {
//        Event event1 = createEvent("event1");
//        Event event2 = createEvent("event2");
//
//        registerUsersToQueue(event1.getId(), 20);
//        registerUsersToQueue(event2.getId(), 20);
//
//        await().atMost(3, TimeUnit.SECONDS).untilAsserted(() -> {
//            List<OneHundredEventParticipant> p1 = participantRepository.findByEventId(event1.getId());
//            List<OneHundredEventParticipant> p2 = participantRepository.findByEventId(event2.getId());
//
//            log.info("[Test] event1 처리 수: {}, event2 처리 수: {}", p1.size(), p2.size());
//
//            assertThat(p1).hasSize(20);
//            assertThat(p2).hasSize(20);
//        });
//    }
//}
