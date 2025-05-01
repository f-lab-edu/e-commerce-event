package com.flab.ecommerce.domain.event.onehundredevent.controller;


import com.flab.ecommerce.domain.event.onehundredevent.dto.EventLogResponseDTO;
import com.flab.ecommerce.domain.event.onehundredevent.service.OneHundredEventService;
import com.flab.ecommerce.global.response.ApiResponse;
import com.flab.ecommerce.global.security.provider.LoginMemberProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
@Tag(name = "100원 이벤트", description = "100원 이벤트 참가 및 관리 기능을 제공합니다.")
@RestController
@RequestMapping("/events/{eventId}")
@RequiredArgsConstructor
public class OneHundredEventController {

    private final OneHundredEventService oneHundredEventService;
    private final LoginMemberProvider loginMemberProvider;

   /* @Operation(summary = "100원 이벤트 참가 신청", description = "사용자가 100원 이벤트에 참가 신청하여 대기열에 추가됩니다.")
    @PostMapping("/queue")
    public ResponseEntity<ApiResponse<String>> participate(
            @PathVariable Long eventId, Principal principal) {
        Long memberId = loginMemberProvider.getLoginMemberId(principal);
        oneHundredEventService.addToQueue(eventId, memberId);
        return ResponseEntity.ok(ApiResponse.success("참가 신청이 완료되었습니다."));
    }*/

    @PostMapping("/queuetest")
    public ResponseEntity<ApiResponse<String>> participatetest(
            @PathVariable Long eventId,
            @RequestParam Long memberId) {
        oneHundredEventService.addToQueue(eventId, memberId);
        return ResponseEntity.ok(ApiResponse.success("참가 신청이 완료되었습니다."));
    }

    //Todo: 결제 시스템 붙인 후 API 제거 필요, 결제 완료 후 자동 확정으로 변경
    @Operation(summary = "100원 이벤트 참가 확정", description = "")
    @PostMapping("/confirm")
    public ResponseEntity<ApiResponse<String>> confirmParticipation(
            @PathVariable Long eventId, Principal principal) {
        Long memberId = loginMemberProvider.getLoginMemberId(principal);
        oneHundredEventService.confirmParticipation(eventId, memberId);
        return ResponseEntity.ok(ApiResponse.success("참여가 확정되었습니다."));
    }
    @Operation(summary = "100원 이벤트 로그 조회 (관리자 전용)", description = "이벤트의 참가자 현황 및 로그를 조회합니다.")
    @GetMapping("/logs")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<EventLogResponseDTO>> getEventLogs(@PathVariable Long eventId) {
        EventLogResponseDTO eventLogs = oneHundredEventService.getEventLogs(eventId);
        return ResponseEntity.ok(ApiResponse.success(eventLogs));
    }



}
