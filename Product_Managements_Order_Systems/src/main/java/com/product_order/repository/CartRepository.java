package com.product_order.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.product_order.entity.Cart;

public interface CartRepository extends JpaRepository<Cart, Long> {
	Optional<Cart> findByUserId(Long userId);
}