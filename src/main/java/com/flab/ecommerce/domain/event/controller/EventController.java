package com.flab.ecommerce.domain.event.controller;

import com.flab.ecommerce.domain.event.dto.EventCreateRequestDTO;
import com.flab.ecommerce.domain.event.dto.EventDetailResponseDTO;
import com.flab.ecommerce.domain.event.dto.EventUpdateRequestDTO;
import com.flab.ecommerce.domain.event.service.EventService;
import com.flab.ecommerce.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

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
}
