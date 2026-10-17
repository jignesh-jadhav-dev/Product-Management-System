package com.product_order.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.product_order.entity.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}