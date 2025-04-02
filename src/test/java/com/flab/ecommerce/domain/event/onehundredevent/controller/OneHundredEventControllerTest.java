package com.flab.ecommerce.domain.event.onehundredevent.controller;

import com.flab.ecommerce.domain.event.entity.Event;
import com.flab.ecommerce.domain.event.enums.EventStatus;
import com.flab.ecommerce.domain.event.enums.EventType;
import com.flab.ecommerce.domain.event.onehundredevent.consumer.OneHundredEventConsumer;
import com.flab.ecommerce.domain.event.onehundredevent.entity.OneHundredEventParticipant;
import com.flab.ecommerce.domain.event.onehundredevent.policy.OneHundredEventPolicy;
import com.flab.ecommerce.domain.event.onehundredevent.repository.OneHundredEventRepository;
import com.flab.ecommerce.domain.event.onehundredevent.service.OneHundredEventService;
import com.flab.ecommerce.domain.event.queue.EventQueue;
import com.flab.ecommerce.domain.event.repository.EventRepository;
import com.flab.ecommerce.domain.member.entity.Member;
import com.flab.ecommerce.domain.member.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.flab.ecommerce.domain.member.enums.MemberRole.USER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
//@Transactional
class OneHundredEventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private OneHundredEventService oneHundredEventService;

    @Autowired
    private OneHundredEventConsumer oneHundredEventConsumer;

    @Autowired
    private OneHundredEventRepository participantRepository;

    @Autowired
    private EventQueue eventQueue;

    @Autowired
    private OneHundredEventPolicy eventPolicy;

    private Long memberId;
    private Long eventId;

    @BeforeEach
    void setUp() {
        memberRepository.deleteAll();

        Member member = Member.builder()
                .email("test@email.com")
                .password("password")
                .name("test")
                .role(USER)
                .build();
        memberRepository.save(member);
        this.memberId = member.getId();

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
        this.eventId = event.getId();
    }

    @Test
    @DisplayName("컨트롤러 + 컨슈머 전체 흐름 테스트 - 큐 등록부터 정책 적용까지")
    void 컨트롤러_큐등록_후_컨슈머가_정책처리() throws Exception {

        // when: 컨트롤러 API로 큐 등록 요청
        mockMvc.perform(post("/events/{eventId}/queue", eventId)
                        .with(user("test@email.com").roles("USER")))
                .andExpect(status().isOk());

        // then: 큐에 들어갔는지 확인
        assertThat(eventQueue.isUserInQueue(eventId, memberId)).isTrue();

        // when: consumeQueue 호출해서 실제 이벤트 처리
        oneHundredEventConsumer.consumeQueue();

        // then: DB에 저장되었는지 확인(3초 안에 조건이 만족되면 바로 통과)
        await()
                .atMost(3, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    List<OneHundredEventParticipant> participants = participantRepository.findByEventId(eventId);
                    assertThat(participants).hasSize(1);
                });

    }
}