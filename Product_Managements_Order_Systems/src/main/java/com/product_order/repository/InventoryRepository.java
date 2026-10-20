package com.product_order.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.product_order.entity.Inventory;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
	Optional<Inventory> findByProductId(Long productId);
}
