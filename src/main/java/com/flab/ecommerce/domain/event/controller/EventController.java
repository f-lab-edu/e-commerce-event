package com.flab.ecommerce.domain.event.controller;

import com.flab.ecommerce.domain.event.dto.EventCreateRequestDTO;
import com.flab.ecommerce.domain.event.dto.EventDetailResponseDTO;
import com.flab.ecommerce.domain.event.dto.EventListResponseDTO;
import com.flab.ecommerce.domain.event.dto.EventUpdateRequestDTO;
import com.flab.ecommerce.domain.event.enums.EventStatus;
import com.flab.ecommerce.domain.event.service.EventService;
import com.flab.ecommerce.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "이벤트", description = "이벤트 조회 및 관리 기능을 제공합니다.")
@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @Operation(summary = "이벤트 전체 조회", description = "상태별 필터링이 가능한 전체 이벤트 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<List<EventListResponseDTO>>> getAllEvents(@RequestParam(required = false) EventStatus status) {
        List<EventListResponseDTO> events = eventService.getAllEvents(status);
        return ResponseEntity.ok(ApiResponse.success(events));
    }

    @Operation(summary = "이벤트 상세 조회", description = "이벤트 ID로 상세 정보를 조회합니다.")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EventDetailResponseDTO>> getEventById(@PathVariable long id) {
        EventDetailResponseDTO event = eventService.getEventById(id);
        return ResponseEntity.ok(ApiResponse.success(event));
    }

    @Operation(summary = "이벤트 등록 (관리자 전용)", description = "관리자가 새로운 이벤트를 등록합니다.")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<EventDetailResponseDTO>> createEvent(@RequestBody @Valid EventCreateRequestDTO requestDTO) {
        EventDetailResponseDTO event = eventService.createEvent(requestDTO);
        return ResponseEntity.ok(ApiResponse.success(event));
    }

    @Operation(summary = "이벤트 수정 (관리자 전용)", description = "관리자가 이벤트 정보를 수정합니다.")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<EventDetailResponseDTO>> updateEvent(@PathVariable long id, @RequestBody @Valid EventUpdateRequestDTO requestDTO) {
        EventDetailResponseDTO event = eventService.updateEvent(id, requestDTO);
        return ResponseEntity.ok(ApiResponse.success(event));
    }

    @Operation(summary = "이벤트 상태 변경 (관리자 전용)", description = "관리자가 이벤트 상태를 변경합니다.")
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<EventDetailResponseDTO>> changeEventStatus(@PathVariable long id, @RequestParam EventStatus status) {
        EventDetailResponseDTO event = eventService.changeEventStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success(event));
    }

    @Operation(summary = "이벤트 삭제 (관리자 전용)", description = "관리자가 이벤트를 삭제합니다.")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> deleteEvent(@PathVariable long id) {
        eventService.deleteEvent(id);
        return ResponseEntity.ok(ApiResponse.success("이벤트 삭제가 완료 되었습니다."));
    }


}
