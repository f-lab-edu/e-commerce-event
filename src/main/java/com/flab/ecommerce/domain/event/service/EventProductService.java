package com.flab.ecommerce.domain.event.service;

import com.flab.ecommerce.domain.event.dto.EventProductCreateRequestDTO;
import com.flab.ecommerce.domain.event.dto.EventProductResponseDTO;
import com.flab.ecommerce.domain.event.entity.Event;
import com.flab.ecommerce.domain.event.entity.EventProduct;
import com.flab.ecommerce.domain.event.exception.EventNotFoundException;
import com.flab.ecommerce.domain.event.repository.EventProductRepository;
import com.flab.ecommerce.domain.event.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventProductService {

    private final EventRepository eventRepository;
    private final EventProductRepository eventProductRepository;

    @Transactional
    public void addProductToEvent(Long eventId, EventProductCreateRequestDTO requestDTO) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(EventNotFoundException::new);

        EventProduct product = EventProduct.builder()
                .event(event)
                .name(requestDTO.getName())
                .price(requestDTO.getPrice())
                .description(requestDTO.getDescription())
                .build();

        eventProductRepository.save(product);
    }

    @Transactional(readOnly = true)
    public List<EventProductResponseDTO> getProductsByEvent(Long eventId) {
        List<EventProduct> products = eventProductRepository.findByEventId(eventId);
        return products.stream().map(EventProductResponseDTO::new).toList();
    }
}
