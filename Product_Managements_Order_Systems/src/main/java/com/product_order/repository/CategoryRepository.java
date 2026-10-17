package com.product_order.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.product_order.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
	boolean existsByNameIgnoreCase(String name);
}