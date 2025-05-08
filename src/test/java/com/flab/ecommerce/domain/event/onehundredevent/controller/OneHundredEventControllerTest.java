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
//import org.junit.jupiter.api.BeforeEach;
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
//@SpringBootTest
//@AutoConfigureMockMvc
////@Transactional
//class OneHundredEventControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private EventRepository eventRepository;
//
//    @Autowired
//    private MemberRepository memberRepository;
//
//    @Autowired
//    private OneHundredEventRepository participantRepository;
//
//    @Autowired
//    private EventQueue eventQueue;
//
//    private Long memberId;
//    private Long eventId;
//
//    @BeforeEach
//    void setUp() {
//        memberRepository.deleteAll();
//
//        Member member = Member.builder()
//                .email("test@email.com")
//                .password("password")
//                .name("test")
//                .role(USER)
//                .build();
//        memberRepository.save(member);
//        this.memberId = member.getId();
//
//        Event event = Event.builder()
//                .type(EventType.ONE_HUNDRED_WON_DEAL)
//                .startDate(LocalDateTime.now().minusMinutes(1))
//                .endDate(LocalDateTime.now().plusMinutes(10))
//                .maxParticipants(100)
//                .status(EventStatus.ACTIVE)
//                .name("100원딜")
//                .description("테스트")
//                .build();
//        eventRepository.save(event);
//        this.eventId = event.getId();
//    }
//
//    @Test
//    @DisplayName("한 명 참가자 흐름 테스트")
//    void 단일_사용자_이벤트_참여_전체_흐름_검증() throws Exception {
//
//        // when: 컨트롤러 API로 큐 등록 요청
//        mockMvc.perform(post("/events/{eventId}/queue", eventId)
//                        .with(user("test@email.com").roles("USER")))
//                .andExpect(status().isOk());
//
//        // then: 큐에 들어갔는지 확인
//        assertThat(eventQueue.isUserInQueue(eventId, memberId)).isTrue();
//
//        // then: DB에 저장되었는지 확인(3초 안에 조건이 만족되면 바로 통과)
//        await()
//                .atMost(3, TimeUnit.SECONDS)
//                .untilAsserted(() -> {
//                    List<OneHundredEventParticipant> participants = participantRepository.findByEventId(eventId);
//                    assertThat(participants).hasSize(1);
//                });
//
//    }
//
//    @Test
//    @DisplayName("여러 사용자 큐 등록 및 DB 저장 검증")
//    void 여러_사용자_이벤트_참여_및_정책처리_검증() throws Exception {
//        int userCount = 10;
//
//        for (int i = 0; i < userCount; i++) {
//            Member member = Member.builder()
//                    .email("test" + i + "@email.com")
//                    .password("password")
//                    .name("test" + i)
//                    .role(USER)
//                    .build();
//            memberRepository.save(member);
//
//            mockMvc.perform(post("/events/{eventId}/queue", eventId)
//                            .with(user(member.getEmail()).roles("USER")))
//                    .andExpect(status().isOk());
//        }
//
//        // 3초 내에 userCount 명 저장되었는지 확인
//        await().atMost(3, TimeUnit.SECONDS)
//                .untilAsserted(() -> {
//                    List<OneHundredEventParticipant> participants = participantRepository.findByEventId(eventId);
//                    assertThat(participants).hasSize(userCount);
//                });
//    }
//
//    @Test
//    @DisplayName("여러 이벤트에 각각 사용자 참여 시 독립적으로 처리되는지 테스트")
//    void 여러_이벤트_독립_정책_처리_테스트() throws Exception {
//        // given - 두 개 이벤트 생성
//        Event event1 = Event.builder()
//                .type(EventType.ONE_HUNDRED_WON_DEAL)
//                .startDate(LocalDateTime.now().minusMinutes(1))
//                .endDate(LocalDateTime.now().plusMinutes(10))
//                .maxParticipants(100)
//                .status(EventStatus.ACTIVE)
//                .name("이벤트1")
//                .description("이벤트1")
//                .build();
//        Event event2 = Event.builder()
//                .type(EventType.ONE_HUNDRED_WON_DEAL)
//                .startDate(LocalDateTime.now().minusMinutes(1))
//                .endDate(LocalDateTime.now().plusMinutes(10))
//                .maxParticipants(100)
//                .status(EventStatus.ACTIVE)
//                .name("이벤트2")
//                .description("이벤트2")
//                .build();
//        eventRepository.save(event1);
//        eventRepository.save(event2);
//
//        Long eventId1 = event1.getId();
//        Long eventId2 = event2.getId();
//
//        // 각각 사용자 생성 및 큐 등록
//        Member user1 = memberRepository.save(Member.builder()
//                .email("user1@email.com")
//                .password("pw")
//                .name("user1")
//                .role(USER)
//                .build());
//
//        Member user2 = memberRepository.save(Member.builder()
//                .email("user2@email.com")
//                .password("pw")
//                .name("user2")
//                .role(USER)
//                .build());
//
//        mockMvc.perform(post("/events/{eventId}/queue", eventId1)
//                        .with(user(user1.getEmail()).roles("USER")))
//                .andExpect(status().isOk());
//
//        mockMvc.perform(post("/events/{eventId}/queue", eventId2)
//                        .with(user(user2.getEmail()).roles("USER")))
//                .andExpect(status().isOk());
//
//        await().atMost(3, TimeUnit.SECONDS).untilAsserted(() -> {
//            List<OneHundredEventParticipant> participants1 = participantRepository.findByEventId(eventId1);
//            List<OneHundredEventParticipant> participants2 = participantRepository.findByEventId(eventId2);
//
//            assertThat(participants1).hasSize(1);
//            assertThat(participants1.get(0).getMemberId()).isEqualTo(user1.getId());
//
//            assertThat(participants2).hasSize(1);
//            assertThat(participants2.get(0).getMemberId()).isEqualTo(user2.getId());
//        });
//    }
//
//    @Test
//    @DisplayName("여러 사용자 - 여러 이벤트 독립 처리 테스트")
//    void 여러_사용자_여러_이벤트_참여_검증() throws Exception {
//        int userCount = 5;
//
//        // 이벤트 2개 생성
//        Event event1 = Event.builder()
//                .type(EventType.ONE_HUNDRED_WON_DEAL)
//                .startDate(LocalDateTime.now().minusMinutes(1))
//                .endDate(LocalDateTime.now().plusMinutes(10))
//                .maxParticipants(100)
//                .status(EventStatus.ACTIVE)
//                .name("이벤트1")
//                .description("이벤트1 설명")
//                .build();
//        Event event2 = Event.builder()
//                .type(EventType.ONE_HUNDRED_WON_DEAL)
//                .startDate(LocalDateTime.now().minusMinutes(1))
//                .endDate(LocalDateTime.now().plusMinutes(10))
//                .maxParticipants(100)
//                .status(EventStatus.ACTIVE)
//                .name("이벤트2")
//                .description("이벤트2 설명")
//                .build();
//        eventRepository.saveAll(List.of(event1, event2));
//
//        Long eventId1 = event1.getId();
//        Long eventId2 = event2.getId();
//
//        // 각각의 이벤트에 userCount만큼 사용자 생성 & 큐 등록
//        for (int i = 0; i < userCount; i++) {
//            Member user1 = memberRepository.save(Member.builder()
//                    .email("event1_user" + i + "@email.com")
//                    .password("pw")
//                    .name("유저" + i)
//                    .role(USER)
//                    .build());
//
//            Member user2 = memberRepository.save(Member.builder()
//                    .email("event2_user" + i + "@email.com")
//                    .password("pw")
//                    .name("유저" + (i + userCount))
//                    .role(USER)
//                    .build());
//
//            mockMvc.perform(post("/events/{eventId}/queue", eventId1)
//                            .with(user(user1.getEmail()).roles("USER")))
//                    .andExpect(status().isOk());
//
//            mockMvc.perform(post("/events/{eventId}/queue", eventId2)
//                            .with(user(user2.getEmail()).roles("USER")))
//                    .andExpect(status().isOk());
//        }
//
//        // then - 각각 이벤트에 대해 사용자 수가 정확히 들어갔는지 검증
//        await().atMost(3, TimeUnit.SECONDS).untilAsserted(() -> {
//            List<OneHundredEventParticipant> participants1 = participantRepository.findByEventId(eventId1);
//            List<OneHundredEventParticipant> participants2 = participantRepository.findByEventId(eventId2);
//
//            assertThat(participants1).hasSize(userCount);
//            assertThat(participants2).hasSize(userCount);
//        });
//    }
//
////    @Test
////    @DisplayName("컨슈머는 가장 큐에 많이 쌓인 이벤트 하나만 처리한다")
////    void 가장_많이_쌓인_이벤트_하나만_처리() throws Exception {
////        // 이벤트 2개 생성
////        Event event1 = eventRepository.save(Event.builder()
////                .type(EventType.ONE_HUNDRED_WON_DEAL)
////                .startDate(LocalDateTime.now().minusMinutes(1))
////                .endDate(LocalDateTime.now().plusMinutes(10))
////                .maxParticipants(100)
////                .status(EventStatus.ACTIVE)
////                .name("이벤트1")
////                .description("설명1")
////                .build());
////
////        Event event2 = eventRepository.save(Event.builder()
////                .type(EventType.ONE_HUNDRED_WON_DEAL)
////                .startDate(LocalDateTime.now().minusMinutes(1))
////                .endDate(LocalDateTime.now().plusMinutes(10))
////                .maxParticipants(100)
////                .status(EventStatus.ACTIVE)
////                .name("이벤트2")
////                .description("설명2")
////                .build());
////
////        // 이벤트1에는 3명, 이벤트2에는 5명 등록
////        for (int i = 0; i < 3; i++) {
////            Member member = memberRepository.save(Member.builder()
////                    .email("e1_user" + i + "@email.com")
////                    .password("pw")
////                    .name("e1_user" + i)
////                    .role(USER)
////                    .build());
////
////            mockMvc.perform(post("/events/{eventId}/queue", event1.getId())
////                            .with(user(member.getEmail()).roles("USER")))
////                    .andExpect(status().isOk());
////        }
////
////        for (int i = 0; i < 5; i++) {
////            Member member = memberRepository.save(Member.builder()
////                    .email("e2_user" + i + "@email.com")
////                    .password("pw")
////                    .name("e2_user" + i)
////                    .role(USER)
////                    .build());
////
////            mockMvc.perform(post("/events/{eventId}/queue", event2.getId())
////                            .with(user(member.getEmail()).roles("USER")))
////                    .andExpect(status().isOk());
////        }
////
////        // then - 큐 사이즈가 더 큰 이벤트2만 처리됐는지 확인
////        await().atMost(3, TimeUnit.SECONDS).untilAsserted(() -> {
////            List<OneHundredEventParticipant> e1Participants = participantRepository.findByEventId(event1.getId());
////            List<OneHundredEventParticipant> e2Participants = participantRepository.findByEventId(event2.getId());
////
////            assertThat(e1Participants).isEmpty(); // 이벤트1은 처리 안 됨
////            assertThat(e2Participants).hasSize(5); // 이벤트2는 처리됨
////        });
////    }
//}