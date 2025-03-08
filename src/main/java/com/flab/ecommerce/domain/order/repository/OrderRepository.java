package com.flab.ecommerce.domain.order.repository;

import com.flab.ecommerce.domain.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByMemberIdOrderByIdDesc(Long memberId);
    Optional<Order> findById(Long orderId);
}
