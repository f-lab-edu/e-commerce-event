package com.flab.ecommerce.domain.event.controller;

import com.flab.ecommerce.domain.event.dto.EventCreateRequestDTO;
import com.flab.ecommerce.domain.event.dto.EventDetailResponseDTO;
import com.flab.ecommerce.domain.event.dto.EventListResponseDTO;
import com.flab.ecommerce.domain.event.dto.EventUpdateRequestDTO;
import com.flab.ecommerce.domain.event.enums.EventStatus;
import com.flab.ecommerce.domain.event.service.EventService;
import com.flab.ecommerce.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<EventListResponseDTO>>> getAllEvents(@RequestParam(required = false) EventStatus status) {
        List<EventListResponseDTO> events = eventService.getAllEvents(status);
        return ResponseEntity.ok(ApiResponse.success(events));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EventDetailResponseDTO>> getEventById(@PathVariable long id) {
        EventDetailResponseDTO event = eventService.getEventById(id);
        return ResponseEntity.ok(ApiResponse.success(event));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<EventDetailResponseDTO>> createEvent(@RequestBody EventCreateRequestDTO requestDTO) {
        EventDetailResponseDTO event = eventService.createEvent(requestDTO);
        return ResponseEntity.ok(ApiResponse.success(event));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<EventDetailResponseDTO>> updateEvent(@PathVariable long id, @RequestBody EventUpdateRequestDTO requestDTO) {
        EventDetailResponseDTO event = eventService.updateEvent(id, requestDTO);
        return ResponseEntity.ok(ApiResponse.success(event));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<EventDetailResponseDTO>> changeEventStatus(@PathVariable long id, @RequestParam EventStatus status) {
        EventDetailResponseDTO event = eventService.changeEventStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success(event));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> deleteEvent(@PathVariable long id) {
        eventService.deleteEvent(id);
        return ResponseEntity.ok(ApiResponse.success("이벤트 삭제가 완료 되었습니다."));
    }


}
