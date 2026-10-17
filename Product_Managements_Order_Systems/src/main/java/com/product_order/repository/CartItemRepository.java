package com.product_order.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.product_order.entity.CartItem;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
}