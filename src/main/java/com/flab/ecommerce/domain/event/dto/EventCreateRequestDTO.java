package com.flab.ecommerce.domain.event.dto;

import com.flab.ecommerce.domain.event.enums.EventType;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class EventCreateRequestDTO {

    @NotBlank(message = "이벤트 이름은 필수입니다.")
    private String name;

    @NotBlank(message = "이벤트 설명은 필수입니다.")
    private String description;

    @NotNull(message = "이벤트 타입은 필수입니다.")
    private EventType type;

    @NotNull(message = "이벤트 시작일은 필수입니다.")
    @Future(message = "이벤트 시작일은 미래 날짜여야 합니다.")
    private LocalDateTime startDate;

    @NotNull(message = "이벤트 종료일은 필수입니다.")
    @Future(message = "이벤트 종료일은 미래 날짜여야 합니다.")
    private LocalDateTime endDate;
}
