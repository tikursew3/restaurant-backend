package com.passion2code.restaurant_backend.repository;

import com.passion2code.restaurant_backend.model.Order;
import com.passion2code.restaurant_backend.enums.OrderStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByOrderStatus(OrderStatus status);

    List<Order> findByCreatedAtBetween(LocalDateTime from, LocalDateTime to);

    // Handy when you want order + items in one go
    @EntityGraph(attributePaths = {"orderItems", "payment"})
    Optional<Order> findWithOrderItemsById(Long id);
}
