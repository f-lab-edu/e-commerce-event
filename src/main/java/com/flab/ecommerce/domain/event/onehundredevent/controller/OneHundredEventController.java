package com.flab.ecommerce.domain.event.onehundredevent.controller;


import com.flab.ecommerce.domain.event.onehundredevent.service.OneHundredEventService;
import com.flab.ecommerce.global.response.ApiResponse;
import com.flab.ecommerce.global.security.provider.LoginMemberProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/events/{eventId}")
@RequiredArgsConstructor
public class OneHundredEventController {

    private final OneHundredEventService oneHundredEventService;
    private final LoginMemberProvider loginMemberProvider;

    @PostMapping("/queue")
    public ResponseEntity<ApiResponse<String>> participate(
            @PathVariable Long eventId, Principal principal) {
        Long memberId = loginMemberProvider.getLoginMemberId(principal);
        oneHundredEventService.addToQueue(eventId, memberId);
        return ResponseEntity.ok(ApiResponse.success("참가 신청이 완료되었습니다."));
    }


}
